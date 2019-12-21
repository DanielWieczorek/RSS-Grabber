package de.wieczorek.rss.trading.business;

import de.wieczorek.rss.core.timer.RecurrentTask;
import de.wieczorek.rss.trading.common.io.DataGenerator;
import de.wieczorek.rss.trading.common.io.DataGeneratorBuilder;
import de.wieczorek.rss.trading.common.oracle.*;
import de.wieczorek.rss.trading.common.oracle.comparison.Comparison;
import de.wieczorek.rss.trading.common.trading.Trade;
import de.wieczorek.rss.trading.common.trading.TradingSimulationResult;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TrainingTimer.class);
    private static final int NUMBER_OF_BUYSELL_CONFIGURATIONS = 3;
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

        TradingSimulationResult simulationResult = simulator.simulate(generator, oracle);
        List<Trade> trades = simulationResult.getTrades();
        double tradeProfit = 0;
        int buySellPairs = 0;
        int positiveTrades = 0;
        if (trades.size() >= 1) {

            for (int i = 0; i < trades.size() - 1; i += 2) {
                Trade buy = trades.get(i);
                Trade sell = trades.get(i + 1);

                tradeProfit += sell.getAfter().getEurEquivalent() - buy.getBefore().getEurEquivalent();
                positiveTrades += tradeProfit > 0 ? 1 : 0;
                buySellPairs++;
            }
        }


        if (trades.size() > 500) {
            Trade lastTrade = trades.get(trades.size() - 1);
            return simulationResult.getFinalBalance().getEurEquivalent();
        }
        return 0;

    }

    private OracleConfiguration buildOracleConfiguration(Genotype<IntegerGene> genes) {
        OracleConfiguration configuration = new OracleConfiguration();


        List<TradeConfiguration> buyConfigurations = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_BUYSELL_CONFIGURATIONS; i++) {
            TradeConfiguration buyConfig = new TradeConfiguration();
            buyConfig.setThreshold(genes.get(0).getGene(i).intValue());
            buyConfig.setAverageTime(genes.get(1).getGene(i).intValue());
            buyConfig.setComparison(Comparison.getValueForIndex(genes.get(2).getGene(i).intValue()));
            buyConfig.setOffset(genes.get(3).getGene(i).intValue());
            buyConfigurations.add(buyConfig);

        }
        configuration.setBuyConfigurations(buyConfigurations);

        List<Operator> buyOperators = new ArrayList<>();

        configuration.setBuyOperators(genes.get(4).
                stream()
                .map(gene -> Operator.getValueForIndex(gene.intValue()))
                .collect(Collectors.toList()));

        List<TradeConfiguration> sellConfigurations = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_BUYSELL_CONFIGURATIONS; i++) {
            TradeConfiguration sellConfig = new TradeConfiguration();
            sellConfig.setThreshold(genes.get(5).getGene(i).intValue());
            sellConfig.setAverageTime(genes.get(6).getGene(i).intValue());
            sellConfig.setComparison(Comparison.getValueForIndex(genes.get(7).getGene(i).intValue()));
            sellConfig.setOffset(genes.get(8).getGene(i).intValue());
            sellConfigurations.add(sellConfig);
        }
        configuration.setSellConfigurations(sellConfigurations);

        configuration.setSellOperators(genes.get(9).
                stream()
                .map(gene -> Operator.getValueForIndex(gene.intValue()))
                .collect(Collectors.toList()));


        if (genes.get(10).getGene(0).intValue() == 1) {
            StopLossConfiguration stopLossConfig = new StopLossConfiguration();
            stopLossConfig.setStopLossThreshold(genes.get(11).getGene(0).intValue());
            stopLossConfig.setStopLossCooldown(genes.get(12).getGene(0).intValue());
            configuration.setStopLossConfiguration(Optional.of(stopLossConfig));
        }
        return configuration;
    }

    private void update(final EvolutionResult<IntegerGene, Double> result) {
        if (best == null || best.compareTo(result.getBestPhenotype()) < 0) {
            best = result.getBestPhenotype();

            configurationDao.write(buildOracleConfiguration(result.getBestPhenotype().getGenotype()));
        }
        System.out.println("best phenotype of generation " + result.getGeneration() + ": " + result.getBestPhenotype());
        System.out.println("average fitness: " + result.getPopulation().stream().filter(phenotype -> phenotype.getFitness() > 0).map(Phenotype::fitnessOptional).map(Optional::get).collect(Collectors.averagingDouble(Double::doubleValue)));
        System.out.println("evaluation for " + result.getPopulation().length() + " individuals took: " + result.getDurations().getEvaluationDuration().toSeconds());

    }

    @Override
    public void run() {
        try {
            generator = generatorBuilder.produceGenerator();

            Factory<Genotype<IntegerGene>> gtf =
                    Genotype.of(IntegerChromosome.of(-200, 200, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //0 buy thresholds
                            IntegerChromosome.of(1, 480, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //3 duration of the averaging
                            IntegerChromosome.of(0, Comparison.values().length - 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //1 below/above for buy
                            IntegerChromosome.of(1, 1440 - 480, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //2 offset
                            IntegerChromosome.of(0, Operator.values().length - 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS - 1)), //4 operators

                            IntegerChromosome.of(-200, 200, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //5 sell thresholds
                            IntegerChromosome.of(1, 480, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //8 duration of the averaging
                            IntegerChromosome.of(0, Comparison.values().length - 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //7 below/above for sell
                            IntegerChromosome.of(1, 1440 - 480, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //6 offset
                            IntegerChromosome.of(0, Operator.values().length - 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS - 1)), //9 operators

                            IntegerChromosome.of(0, 1, IntRange.of(1)), //10 is stop-loss activated
                            IntegerChromosome.of(0, 200, IntRange.of(1)), //11 stop-loss threshold
                            IntegerChromosome.of(0, 120, IntRange.of(1))); //12 wait time after stop-loss trigger

            Engine<IntegerGene, Double> engine = Engine
                    .builder(this::eval, gtf)
                    .populationSize(100 * 1000)
                    .mapping(EvolutionResult.toUniquePopulation())
                    .executor(Executors.newFixedThreadPool(16))
                    .survivorsFraction(0.3)
                    .survivorsSelector(new EliteSelector(20000, new MonteCarloSelector()))
                    .alterers(new UniformCrossover<>(0.1), new MeanAlterer(0.1)

                    )
                    .offspringSelector(new TournamentSelector())
                    .offspringFraction(0.3)
                    .mapping(EvolutionResult.toUniquePopulation())
                    .optimize(Optimize.MAXIMUM)
                    .build();


            Phenotype<IntegerGene, Double> result = engine.stream()
                    .peek(this::update)
                    .limit(10000)
                    .peek(EvolutionStatistics.ofNumber())
                    .collect(EvolutionResult.toBestPhenotype());

            OracleConfiguration config = buildOracleConfiguration(result.getGenotype());

            Oracle oracle = new DefaultOracle(config);
            TradingSimulationResult simulationResult = simulator.simulate(generator, oracle);
            System.out.println("Number of trades: " + simulationResult.getTrades().size());
            System.out.println("Euro equivalent: " + simulationResult.getFinalBalance().getEurEquivalent());


        } catch (Exception e) {
            logger.error("error while training: ", e);
        }
    }
}
