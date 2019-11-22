package de.wieczorek.rss.trading.business;

import de.wieczorek.rss.core.timer.RecurrentTask;
import de.wieczorek.rss.trading.common.io.DataGenerator;
import de.wieczorek.rss.trading.common.io.DataGeneratorBuilder;
import de.wieczorek.rss.trading.common.oracle.*;
import de.wieczorek.rss.trading.common.oracle.comparison.Comparison;
import de.wieczorek.rss.trading.common.trading.Trade;
import de.wieczorek.rss.trading.common.trading.TradingSimulator;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.util.Factory;
import io.jenetics.util.IntRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TrainingTimer.class);

    Phenotype<IntegerGene, Double> best = null;

    @Inject
    private TradingSimulator simulator;

    @Inject
    private DataGeneratorBuilder generatorBuilder;

    @Inject
    private OracleConfigurationDao configurationDao;

    private DataGenerator generator;

    private double eval(Genotype<IntegerGene> genes) {
        OracleConfiguration configuration = buildOracleConfiguration(genes);

        Oracle oracle = new DefaultOracle(configuration);

        List<Trade> trades = simulator.simulate(generator, oracle);
        double tradeProfit = 0;
        if (trades.size() >= 1) {

            for (int i = 0; i < trades.size() - 1; i += 2) {
                Trade buy = trades.get(i);
                Trade sell = trades.get(i + 1);

                tradeProfit += sell.getAfter().getEurEquivalent() - buy.getAfter().getEurEquivalent();
            }
        }


        if (trades.size() > 500) {
            Trade lastTrade = trades.get(trades.size() - 1);
            return lastTrade.getAfter().getEurEquivalent();
        }
        return -1000.0;

    }

    private OracleConfiguration buildOracleConfiguration(Genotype<IntegerGene> genes) {
        OracleConfiguration configuration = new OracleConfiguration();

        TradeConfiguration buyConfig = new TradeConfiguration();
        buyConfig.setThreshold(genes.get(0).getGene(0).intValue());
        buyConfig.setAverageTime(genes.get(1).getGene(0).intValue());
        buyConfig.setComparison(Comparison.getValueForIndex(genes.get(2).getGene(0).intValue()));

        configuration.setBuyConfiguration(buyConfig);

        if (genes.get(3).getGene(0).intValue() == 1) {
            TradeConfiguration sellConfig = new TradeConfiguration();
            sellConfig.setThreshold(genes.get(0).getGene(1).intValue());
            sellConfig.setAverageTime(genes.get(1).getGene(1).intValue());
            sellConfig.setComparison(Comparison.getValueForIndex(genes.get(2).getGene(1).intValue()));

            configuration.setSellConfiguration(Optional.of(sellConfig));
        }

        if (genes.get(4).getGene(0).intValue() == 1) {
            StopLossConfiguration stopLossConfig = new StopLossConfiguration();
            stopLossConfig.setStopLossThreshold(genes.get(5).getGene(0).intValue());
            configuration.setStopLossConfiguration(Optional.of(stopLossConfig));
        }
        return configuration;
    }

    private void update(final EvolutionResult<IntegerGene, Double> result) {
        if (best == null || best.compareTo(result.getBestPhenotype()) < 0) {
            best = result.getBestPhenotype();
            System.out.println(result.getGeneration() + ": Found Best phenotype: " + best);
            configurationDao.write(buildOracleConfiguration(result.getBestPhenotype().getGenotype()));
        }
    }

    @Override
    public void run() {
        try {
            generator = generatorBuilder.produceGenerator();

            Factory<Genotype<IntegerGene>> gtf =
                    Genotype.of(IntegerChromosome.of(-200, 200, IntRange.of(2)), // buy sell threshold
                            IntegerChromosome.of(1, 1440, IntRange.of(2)), // duration of the averaging
                            IntegerChromosome.of(0, Comparison.values().length - 1, IntRange.of(2)), // below/above for buy sell
                            IntegerChromosome.of(0, 1, IntRange.of(1)), // is selling activated
                            IntegerChromosome.of(0, 1, IntRange.of(1)), // is stop-loss activated
                            IntegerChromosome.of(0, 8000, IntRange.of(1))); // stop-loss threshold

            Engine<IntegerGene, Double> engine = Engine
                    .builder(this::eval, gtf)
                    .populationSize(100 * 10 * 10)
                    .mapping(EvolutionResult.toUniquePopulation())
                    .executor(Executors.newFixedThreadPool(16))
                    .survivorsFraction(0.7)
                    .survivorsSelector(new TournamentSelector<>())
                    .alterers(new Mutator<>(0.25), new UniformCrossover<>(0.1))
                    .build();


            Phenotype<IntegerGene, Double> result = engine.stream()
                    .peek(this::update)
                    .limit(100)
                    .peek(EvolutionStatistics.ofNumber())
                    .collect(EvolutionResult.toBestPhenotype());

            OracleConfiguration config = buildOracleConfiguration(result.getGenotype());

            Oracle oracle = new DefaultOracle(config);
            List<Trade> trades = simulator.simulate(generator, oracle);
            System.out.println("Number of trades: " + trades.size());
            if (trades.size() > 0) {
                System.out.println("Euro equivalent: " + trades.get(trades.size() - 1).getAfter().getEurEquivalent());
            }


        } catch (Exception e) {
            logger.error("error while training network: ", e);
        }
    }
}
