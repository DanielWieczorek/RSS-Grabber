package de.wieczorek.rss.trading.business;

import de.wieczorek.rss.core.timer.RecurrentTask;
import de.wieczorek.rss.trading.common.*;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.Factory;
import io.jenetics.util.IntRange;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTimer.class);

    @Inject
    private TradingSimulator simulator;

    @Inject
    private DataGeneratorBuilder generatorBuilder;

    private DataGenerator generator;

    public TrainingTimer() {

    }

    private double eval(Genotype<IntegerGene> genes) {
        Oracle oracle = null;
        if(genes.get(3).getGene(0).intValue() == 0){
            oracle = new DefaultOracle(genes.get(0).getGene(0).intValue(),
                    genes.get(0).getGene(1).intValue(),
                    genes.get(1).getGene(0).intValue(),
                    genes.get(2).getGene(0).intValue() == 0? Comparison.LOWER: Comparison.GREATER,
                    genes.get(2).getGene(1).intValue() == 0? Comparison.LOWER: Comparison.GREATER);
        } else {
            oracle = new DefaultOracle(genes.get(0).getGene(0).intValue(),
                    genes.get(0).getGene(1).intValue(),
                    genes.get(1).getGene(0).intValue(),
                    genes.get(2).getGene(0).intValue() == 0? Comparison.LOWER: Comparison.GREATER,
                    genes.get(2).getGene(1).intValue() == 0? Comparison.LOWER: Comparison.GREATER,
                    genes.get(4).getGene(0).intValue());
        }

        List<Trade> trades = simulator.simulate(generator,oracle);
        if(trades.size() > 4){
            Trade lastTrade = trades.get(trades.size()-1);
            return  lastTrade.getAfter().getEurEquivalent();
        }
        return 0;

    }

    Phenotype<IntegerGene,Double> best = null;

private  void update(final EvolutionResult<IntegerGene,Double> result) {
    if(best == null || best.compareTo(result.getBestPhenotype())< 0) {
        best = result.getBestPhenotype();
        System.out.println(result.getGeneration()+": FoundBest phenotype: "+ best);
    }
}

    @Override
    public void run() {
        try {
            generator =  generatorBuilder.produceGenerator();

            Factory<Genotype<IntegerGene>> gtf =
                    Genotype.of(IntegerChromosome.of(-100, 100, IntRange.of(2)), // buy sell threshold
                            IntegerChromosome.of(1, 200, IntRange.of(1)), // duration of the averaging
                            IntegerChromosome.of(0, 1, IntRange.of(2)), // below/above for the sell
                            IntegerChromosome.of(0, 1, IntRange.of(1)), // is stop-loss activated
                            IntegerChromosome.of(0, 1000, IntRange.of(1))); // stop-loss threshold

            Engine<IntegerGene, Double> engine = Engine
                    .builder(this::eval, gtf)
                    .populationSize(100*100 * 10)
                    .mapping(EvolutionResult.toUniquePopulation ( ) )
                    .executor(Executors.newFixedThreadPool(16))
                    .survivorsFraction(0.7)
                    .alterers(new Mutator<>(0.1),new MultiPointCrossover<>(0.1))
                    .build();


            Phenotype<IntegerGene,Double> result = engine.stream()
                    .peek(this::update)
                    .limit(100)
                    .peek(EvolutionStatistics.ofNumber())
                    .collect(EvolutionResult.toBestPhenotype());

            Oracle oracle = new DefaultOracle(result.getGenotype().get(0).getGene(0).intValue(),
                    result.getGenotype().get(0).getGene(1).intValue(),
                    result.getGenotype().get(1).getGene(0).intValue(),
                    result.getGenotype().get(2).getGene(0).intValue() == 0? Comparison.LOWER: Comparison.GREATER,
                    result.getGenotype().get(2).getGene(1).intValue() == 0? Comparison.LOWER: Comparison.GREATER);
            List<Trade> trades = simulator.simulate(generator,oracle);
            System.out.println("Number of trades: "+trades.size());
            if(trades.size() > 0) {
                System.out.println("Euro equivalent: "+trades.get(trades.size()-1).getAfter().getEurEquivalent());
            }


        } catch (Exception e) {
            logger.error("error while training network: ", e);
        }
    }
}
