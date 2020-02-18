package de.wieczorek.rss.trading.business;

import io.jenetics.*;
import io.jenetics.util.MSeq;
import io.jenetics.util.Mean;
import io.jenetics.util.RandomRegistry;

import java.util.Random;

public class AllBuySellCrossover<G extends Gene<Integer, G> & Mean<G>, C extends Comparable<? super C>>
        extends Recombinator<G, C> {
    int isActiveOffset = 5;


    public AllBuySellCrossover(final double probability) {
        super(probability, 2);
    }


    public AllBuySellCrossover() {
        this(0.05);
    }

    @Override
    protected int recombine(MSeq<Phenotype<G, C>> population, int[] individuals, long generation) {
        final Random random = RandomRegistry.getRandom();

        final Phenotype<G, C> pt1 = population.get(individuals[0]);
        final Phenotype<G, C> pt2 = population.get(individuals[1]);
        final Genotype<G> gt1 = pt1.getGenotype();
        final Genotype<G> gt2 = pt2.getGenotype();

        int indexForBuyComparator = 2;
        int indexForSellComparator = 10;

        int chromosomeIndex = RandomRegistry.getRandom().nextBoolean() ? indexForBuyComparator : indexForSellComparator;

        final int startChromosomeIndex = chromosomeIndex - 2;
        final int endChromosomeIndex = chromosomeIndex + 5;

        final MSeq<Chromosome<G>> c1 = gt1.toSeq().copy();
        final MSeq<Chromosome<G>> c2 = gt1.toSeq().copy();
        for (int i = startChromosomeIndex; i <= endChromosomeIndex; i++) {
            c1.get(i).toSeq().copy().setAll(gt2.get(i));
            c2.get(i).toSeq().copy().setAll(gt1.get(i));
        }

        population.set(individuals[0], Phenotype.of(Genotype.of(c1), generation));
        population.set(individuals[1], Phenotype.of(Genotype.of(c1), generation));

        return (endChromosomeIndex - startChromosomeIndex + 1) * gt1.get(chromosomeIndex).length() * 2;
    }


}
