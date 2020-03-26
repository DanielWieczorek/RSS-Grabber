package de.wieczorek.rss.trading.business;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import de.wieczorek.core.timer.RecurrentTask;
import de.wieczorek.rss.trading.common.io.DataGenerator;
import de.wieczorek.rss.trading.common.io.DataGeneratorBuilder;
import de.wieczorek.rss.trading.common.oracle.*;
import de.wieczorek.rss.trading.common.oracle.average.AverageType;
import de.wieczorek.rss.trading.common.oracle.comparison.Comparison;
import de.wieczorek.rss.trading.common.trading.Trade;
import de.wieczorek.rss.trading.common.trading.TradingSimulationResult;
import de.wieczorek.rss.trading.common.trading.TradingSimulator;
import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.ext.engine.CyclicEngine;
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

import static io.jenetics.engine.Limits.bySteadyFitness;

@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TrainingTimer.class);
    private static final int NUMBER_OF_BUYSELL_CONFIGURATIONS = 2;
    private static final int DATAPOINTS_PER_SERIES = 5;
    private static final int OFFSET_SAFETY_MARGIN = 10;
    private static int i = 0;
    private Phenotype<IntegerGene, Double> best = null;
    @Inject
    private TradingSimulator simulator;
    @Inject
    private DataGeneratorBuilder generatorBuilder;
    @Inject
    private OracleConfigurationDao configurationDao;
    @Inject
    private EvolutionProgressDao progressDao;

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


        if (trades.size() > generator.getMaxIndex() / 1440 * 2) {
            Trade lastTrade = trades.get(trades.size() - 1);

            return simulationResult.getFinalBalance().getEurEquivalent();
        }
        return Integer.MIN_VALUE;

    }

    private OracleConfiguration buildOracleConfiguration(Genotype<IntegerGene> genes) {
        OracleConfiguration configuration = new OracleConfiguration();


        List<TradeConfiguration> buyConfigurations = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_BUYSELL_CONFIGURATIONS; i++) {
            if (genes.get(7).getGene(i).intValue() == 1) {
                TradeConfiguration buyConfig = new TradeConfiguration();

                for (int j = 0; j < DATAPOINTS_PER_SERIES; j++) {
                    ValuePoint point = new ValuePoint();
                    int index = i * DATAPOINTS_PER_SERIES + j;


                    point.setAverageTime(genes.get(1).getGene(index).intValue());
                    point.setOffset(genes.get(3).getGene(index).intValue());

                    buyConfig.getComparisonPoints().add(point);

                    if (j < DATAPOINTS_PER_SERIES - 1) {
                        buyConfig.getMargins().add(genes.get(0).getGene(index).intValue());
                        buyConfig.getComparisons().add(Comparison.getValueForIndex(genes.get(2).getGene(index).intValue()));
                    }
                }


                buyConfig.setAverageType(AverageType.getValueForIndex(genes.get(4).getGene(i).intValue()));
                buyConfig.setValuesSource(ValuesSource.getValueForIndex(genes.get(5).getGene(i).intValue()));
                buyConfigurations.add(buyConfig);
            }

        }
        configuration.setBuyConfigurations(buyConfigurations);

        configuration.setBuyOperators(genes.get(6).
                stream()
                .map(gene -> Operator.getValueForIndex(gene.intValue()))
                .limit(Math.max(buyConfigurations.size() - 1, 0))
                .collect(Collectors.toList()));

        List<TradeConfiguration> sellConfigurations = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_BUYSELL_CONFIGURATIONS; i++) {
            if (genes.get(15).getGene(i).intValue() == 1) {
                TradeConfiguration sellConfig = new TradeConfiguration();


                for (int j = 0; j < DATAPOINTS_PER_SERIES; j++) {
                    ValuePoint point = new ValuePoint();
                    int index = i * DATAPOINTS_PER_SERIES + j;

                    point.setAverageTime(genes.get(9).getGene(index).intValue());
                    point.setOffset(genes.get(11).getGene(index).intValue());

                    sellConfig.getComparisonPoints().add(point);

                    if (j < DATAPOINTS_PER_SERIES - 1) {
                        sellConfig.getMargins().add(genes.get(8).getGene(index).intValue());
                        sellConfig.getComparisons().add(Comparison.getValueForIndex(genes.get(10).getGene(index).intValue()));
                    }
                }


                sellConfig.setAverageType(AverageType.getValueForIndex(genes.get(12).getGene(i).intValue()));
                sellConfig.setValuesSource(ValuesSource.getValueForIndex(genes.get(13).getGene(i).intValue()));
                sellConfigurations.add(sellConfig);
            }
        }
        configuration.setSellConfigurations(sellConfigurations);

        configuration.setSellOperators(genes.get(14).
                stream()
                .map(gene -> Operator.getValueForIndex(gene.intValue()))
                .limit(Math.max(sellConfigurations.size() - 1, 0))
                .collect(Collectors.toList()));

        return configuration;
    }

    private void update(final EvolutionResult<IntegerGene, Double> result) {
        if (best == null || best.compareTo(result.getBestPhenotype()) < 0) {
            best = result.getBestPhenotype();

            configurationDao.write(buildOracleConfiguration(result.getBestPhenotype().getGenotype()));
        }

        OracleConfiguration configuration = buildOracleConfiguration(result.getBestPhenotype().getGenotype());

        Oracle oracle = new DefaultOracle(configuration);

        TradingSimulationResult simulationResult = simulator.simulate(generator, oracle);
        List<Trade> trades = simulationResult.getTrades();
        double tradeProfit = 0;
        int buySellPairs = 0;
        int positiveTrades = 0;
        double minEuroEquivalent = 0;
        double maxEuroEquivalent = 0;
        if (trades.size() > 1) {
            minEuroEquivalent = trades.get(0).getAfter().getEurEquivalent();
            maxEuroEquivalent = trades.get(0).getAfter().getEurEquivalent();

            for (int i = 0; i < trades.size() - 1; i += 2) {
                Trade buy = trades.get(i);
                Trade sell = trades.get(i + 1);

                tradeProfit += sell.getAfter().getEurEquivalent() - buy.getBefore().getEurEquivalent();
                minEuroEquivalent = Math.min(minEuroEquivalent, sell.getAfter().getEurEquivalent());
                maxEuroEquivalent = Math.max(maxEuroEquivalent, sell.getAfter().getEurEquivalent());
                positiveTrades += tradeProfit > 0 ? 1 : 0;
                buySellPairs++;
            }
        }

        progressDao.write(result);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            System.out.println("best phenotype of generation " + result.getGeneration() + ": " + mapper.writeValueAsString(buildOracleConfiguration(result.getBestPhenotype().getGenotype())) + " -> " + result.getBestPhenotype().getFitness());
            System.out.println("number of trades: " + trades.size());
            System.out.println("winning trade pairs: " + positiveTrades);
            System.out.println("losing trade pairs: " + (buySellPairs - positiveTrades));
            System.out.println("average profit per trade pair (buy, sell): " + tradeProfit / buySellPairs);
            System.out.println("min value Eur EQ: " + minEuroEquivalent);
            System.out.println("max value Eur EQ: " + maxEuroEquivalent);


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("average fitness: " + result.getPopulation().stream().filter(phenotype -> phenotype.getFitness() > 0).map(Phenotype::fitnessOptional).map(Optional::get).collect(Collectors.averagingDouble(Double::doubleValue)));
        System.out.println("evaluation for " + result.getPopulation().length() + " individuals took: " + result.getDurations().getEvaluationDuration().toSeconds());

    }

    @Override
    public void run() {
        try {
            generator = generatorBuilder.produceGenerator();

            EvolutionStreamable<IntegerGene, Double> engine = buildNewEngine();

            EvolutionResult<IntegerGene, Double> lastResult = progressDao.read();
            EvolutionStream<IntegerGene, Double> stream = lastResult != null ? engine.stream(lastResult) : engine.stream();

            Phenotype<IntegerGene, Double> result = stream
                    .limit(bySteadyFitness(1000))
                    .peek(this::update)
                    .peek(EvolutionStatistics.ofNumber())
                    .collect(EvolutionResult.toBestPhenotype());

            OracleConfiguration config = buildOracleConfiguration(result.getGenotype());

            Oracle oracle = new DefaultOracle(config);
            TradingSimulationResult simulationResult = simulator.simulate(generator, oracle);
            System.out.println("Number of trades: " + simulationResult.getTrades().size());
            System.out.println("Euro equivalent: " + simulationResult.getFinalBalance().getEurEquivalent());

            progressDao.delete();

        } catch (Exception e) {
            logger.error("error while training: ", e);
        }
    }

    private EvolutionStreamable<IntegerGene, Double> buildNewEngine() {
        Factory<Genotype<IntegerGene>> gtf =
                Genotype.of(IntegerChromosome.of(-200, 200, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS * DATAPOINTS_PER_SERIES)), //0 buy thresholds
                        IntegerChromosome.of(1, 480, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS * DATAPOINTS_PER_SERIES)), //1 duration of the averaging
                        IntegerChromosome.of(0, Comparison.values().length - 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS * DATAPOINTS_PER_SERIES)), //2 below/above for buy
                        IntegerChromosome.of(1, (1440 - 480 - OFFSET_SAFETY_MARGIN) / DATAPOINTS_PER_SERIES, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS * DATAPOINTS_PER_SERIES)), //3 offset
                        IntegerChromosome.of(0, AverageType.values().length - 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //4 average type
                        IntegerChromosome.of(0, ValuesSource.values().length - 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //5 source of values
                        IntegerChromosome.of(0, Operator.values().length - 1, IntRange.of(Math.max(NUMBER_OF_BUYSELL_CONFIGURATIONS - 1, 1))), //6 operators
                        IntegerChromosome.of(0, 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //7 is buy configuration active

                        IntegerChromosome.of(-200, 200, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS * DATAPOINTS_PER_SERIES)), //8 sell thresholds
                        IntegerChromosome.of(1, 480, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS * DATAPOINTS_PER_SERIES)), //9 duration of the averaging
                        IntegerChromosome.of(0, Comparison.values().length - 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS * DATAPOINTS_PER_SERIES)), //10 below/above for sell
                        IntegerChromosome.of(1, (1440 - 480 - OFFSET_SAFETY_MARGIN) / DATAPOINTS_PER_SERIES, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS * DATAPOINTS_PER_SERIES)), //11 offset
                        IntegerChromosome.of(0, AverageType.values().length - 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //12 average type
                        IntegerChromosome.of(0, ValuesSource.values().length - 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //13 source of values
                        IntegerChromosome.of(0, Operator.values().length - 1, IntRange.of(Math.max(NUMBER_OF_BUYSELL_CONFIGURATIONS - 1, 1))), //14 operators
                        IntegerChromosome.of(0, 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)) //15 is sell configuration active
                );


        int populationSize = 50 * gtf.newInstance().geneCount();
        Engine<IntegerGene, Double> baseEngine = Engine
                .builder(this::eval, gtf)
                .populationSize(populationSize)
                .mapping(EvolutionResult.toUniquePopulation())
                .executor(Executors.newFixedThreadPool(20))
                .survivorsFraction(0.3)
                .survivorsSelector(new TruncationSelector<>())
                .alterers(new Mutator(), new GaussianMutator<>(), new MeanAlterer<>()) // new SingleBuySellCrossover<>(0.1), new AllBuySellCrossover<>(0.05), new BuySellMeanAlterer(0.1)
                .offspringSelector(new TournamentSelector())
                .optimize(Optimize.MAXIMUM)
                .build();


        final Engine<IntegerGene, Double> diversityEngine = Engine.builder(this::eval, gtf)
                .populationSize(populationSize)
                .mapping(EvolutionResult.toUniquePopulation())
                .executor(Executors.newFixedThreadPool(20))
                .survivorsFraction(0.3)
                .survivorsSelector(new TruncationSelector<>())
                .offspringSelector(new TournamentSelector())
                .optimize(Optimize.MAXIMUM)
                .alterers(new Mutator<>(0.5))
                .build();

        final EvolutionStreamable<IntegerGene, Double> engine = CyclicEngine.of(
                baseEngine.limit(() -> Limits.bySteadyFitness(60)),

                diversityEngine.limit(30)
        );

        return engine;

    }
}
