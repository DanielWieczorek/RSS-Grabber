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
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.jenetics.engine.Limits.bySteadyFitness;

@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TrainingTimer.class);
    private static final int NUMBER_OF_BUYSELL_CONFIGURATIONS = 1;
    private static final int DATAPOINTS_PER_SERIES = 4;
    private static final int NUMBER_OF_COMPARATORS = NUMBER_OF_BUYSELL_CONFIGURATIONS * DATAPOINTS_PER_SERIES;
    private static final int TOTAL_NUMBER_OF_DATAPOINTS = NUMBER_OF_BUYSELL_CONFIGURATIONS * DATAPOINTS_PER_SERIES;
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


        if (trades.size() > 0) { //generator.getMaxIndex() / 1440 * 2) {
            Trade lastTrade = trades.get(trades.size() - 1);

            return simulationResult.getFinalBalance().getEurEquivalent();
        }
        return Integer.MIN_VALUE;

    }

    private OracleConfiguration buildOracleConfiguration(Genotype<IntegerGene> genes) {
        OracleConfiguration configuration = new OracleConfiguration();

        final int buyConfigStartIndex = 0;
        final int sellConfigStartIndex = 9;

        List<TradeConfiguration> buyConfigurations = buildTradeConfigurations(genes, buyConfigStartIndex);
        configuration.setBuyConfigurations(buyConfigurations);
        configuration.setBuyOperators(buildOperatorList(genes, buyConfigStartIndex, buyConfigurations));

        List<TradeConfiguration> sellConfigurations = buildTradeConfigurations(genes, sellConfigStartIndex);
        configuration.setSellConfigurations(sellConfigurations);
        configuration.setSellOperators(buildOperatorList(genes, sellConfigStartIndex, sellConfigurations));

        return configuration;
    }

    private List<Operator> buildOperatorList(Genotype<IntegerGene> genes, int startIndex, List<TradeConfiguration> configurations) {
        return genes.get(startIndex + 6).
                stream()
                .map(gene -> Operator.getValueForIndex(gene.intValue()))
                .limit(Math.max(configurations.size() - 1, 0))
                .collect(Collectors.toList());
    }

    private List<TradeConfiguration> buildTradeConfigurations(Genotype<IntegerGene> genes, int startingGene) {
        List<TradeConfiguration> buyConfigurations = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_BUYSELL_CONFIGURATIONS; i++) {
            if (genes.get(startingGene + 7).getGene(i).intValue() == 1) {
                TradeConfiguration buyConfig = new TradeConfiguration();

                for (int j = 0; j < DATAPOINTS_PER_SERIES; j++) {
                    ValuePoint point = new ValuePoint();
                    int index = i * DATAPOINTS_PER_SERIES + j;


                    point.setAverageTime(genes.get(startingGene + 1).getGene(index).intValue());
                    point.setOffset(genes.get(startingGene + 3).getGene(index).intValue());

                    buyConfig.getComparisonPoints().add(point);

                    if (j < DATAPOINTS_PER_SERIES - 1) {
                        buyConfig.getComparisons().add(Comparison.getValueForIndex(genes.get(startingGene + 2).getGene(index).intValue()));

                        if (genes.get(startingGene).getGene(index).intValue() != Comparison.ALWAYS_MATCH.getIndex()) { // TODO make more generic
                            buyConfig.getMargins().add(genes.get(startingGene).getGene(index).intValue());
                        } else {
                            buyConfig.getMargins().add(0);
                        }

                        if (genes.get(startingGene + 2).getGene(index).intValue() == Comparison.RANGE.getIndex()) { // TODO make more generic
                            buyConfig.getRanges().add(genes.get(startingGene + 8).getGene(index).intValue());
                        } else {
                            buyConfig.getRanges().add(0);
                        }
                    }
                }


                buyConfig.setAverageType(AverageType.getValueForIndex(genes.get(startingGene + 4).getGene(i).intValue()));
                buyConfig.setValuesSource(ValuesSource.getValueForIndex(genes.get(startingGene + 5).getGene(i).intValue()));
                buyConfigurations.add(buyConfig);
            }

        }
        return buyConfigurations;
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

    private <G extends Gene<?, IntegerGene>, C extends Comparable<? super C>>
    UnaryOperator<EvolutionResult<IntegerGene, C>> toUniquePopulation() {
        return result -> {
            final Factory<Genotype<IntegerGene>> factory = result
                    .getPopulation().get(0)
                    .getGenotype();

            Map<OracleConfiguration, Phenotype<IntegerGene, C>> configTypeMapping = new HashMap<>();

            result.getPopulation().forEach(genotype -> configTypeMapping.put(buildOracleConfiguration(genotype.getGenotype()), genotype));

            while (configTypeMapping.size() < result.getPopulation().size()) {
                Genotype<IntegerGene> genotype = factory.newInstance();
                configTypeMapping.put(buildOracleConfiguration(genotype), Phenotype.of(genotype, result.getGeneration()));
            }

            return EvolutionResult.of(
                    result.getOptimize(),
                    Stream.concat(configTypeMapping.values().stream(), result.getPopulation().stream())
                            .limit(result.getPopulation().size())
                            .collect(ISeq.toISeq()),
                    result.getGeneration(),
                    result.getTotalGenerations(),
                    result.getDurations(),
                    result.getKillCount(),
                    result.getInvalidCount(),
                    result.getAlterCount()
            );

        };
    }


    private EvolutionStreamable<IntegerGene, Double> buildNewEngine() {


        Factory<Genotype<IntegerGene>> gtf =
                Genotype.of(IntegerChromosome.of(-200, 200, IntRange.of(NUMBER_OF_COMPARATORS)), //0 buy thresholds
                        IntegerChromosome.of(1, 480, IntRange.of(TOTAL_NUMBER_OF_DATAPOINTS)), //1 duration of the averaging
                        IntegerChromosome.of(0, Comparison.values().length - 1, IntRange.of(NUMBER_OF_COMPARATORS)), //2 below/above for buy
                        IntegerChromosome.of(1, (1440 - 480 - OFFSET_SAFETY_MARGIN) / DATAPOINTS_PER_SERIES, IntRange.of(TOTAL_NUMBER_OF_DATAPOINTS)), //3 offset in minutes
                        IntegerChromosome.of(0, AverageType.values().length - 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //4 average type
                        IntegerChromosome.of(0, ValuesSource.values().length - 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //5 source of values
                        IntegerChromosome.of(0, Operator.values().length - 1, IntRange.of(Math.max(NUMBER_OF_BUYSELL_CONFIGURATIONS - 1, 1))), //6 operators
                        IntegerChromosome.of(0, 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //7 is buy configuration active
                        IntegerChromosome.of(0, 200, IntRange.of(NUMBER_OF_COMPARATORS)), //8 second value for comparison


                        IntegerChromosome.of(-200, 200, IntRange.of(NUMBER_OF_COMPARATORS)), //9 sell thresholds
                        IntegerChromosome.of(1, 480, IntRange.of(TOTAL_NUMBER_OF_DATAPOINTS)), //10 duration of the averaging
                        IntegerChromosome.of(0, Comparison.values().length - 1, IntRange.of(NUMBER_OF_COMPARATORS)), //11 below/above for sell
                        IntegerChromosome.of(1, (1440 - 480 - OFFSET_SAFETY_MARGIN) / DATAPOINTS_PER_SERIES, IntRange.of(TOTAL_NUMBER_OF_DATAPOINTS)), //12 offset in minutes
                        IntegerChromosome.of(0, AverageType.values().length - 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //13 average type
                        IntegerChromosome.of(0, ValuesSource.values().length - 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //14 source of values
                        IntegerChromosome.of(0, Operator.values().length - 1, IntRange.of(Math.max(NUMBER_OF_BUYSELL_CONFIGURATIONS - 1, 1))), //15 operators
                        IntegerChromosome.of(0, 1, IntRange.of(NUMBER_OF_BUYSELL_CONFIGURATIONS)), //16 is sell configuration active
                        IntegerChromosome.of(0, 200, IntRange.of(NUMBER_OF_COMPARATORS)) //17 second value for comparison

                );


        int populationSize = 50 * gtf.newInstance().geneCount();
        Engine<IntegerGene, Double> baseEngine = Engine
                .builder(this::eval, gtf)
                .populationSize(populationSize)
                .mapping(toUniquePopulation())
                .executor(Executors.newFixedThreadPool(20))
                .survivorsFraction(0.3)
                .survivorsSelector(new TruncationSelector<>())
                .alterers(new Mutator(), new GaussianMutator<>(), new MeanAlterer<>()) // new SingleBuySellCrossover<>(0.1), new AllBuySellCrossover<>(0.05), new BuySellMeanAlterer(0.1)
                .offspringSelector(new TournamentSelector())
                .optimize(Optimize.MAXIMUM)
                .build();


        final Engine<IntegerGene, Double> diversityEngine = Engine.builder(this::eval, gtf)
                .populationSize(populationSize)
                .mapping(toUniquePopulation())
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
