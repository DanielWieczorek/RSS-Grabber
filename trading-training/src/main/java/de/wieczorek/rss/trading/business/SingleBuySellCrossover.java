package de.wieczorek.rss.trading.business;

import io.jenetics.*;
import io.jenetics.util.MSeq;
import io.jenetics.util.Mean;
import io.jenetics.util.RandomRegistry;

import java.util.Random;

public class SingleBuySellCrossover<G extends Gene<Integer, G> & Mean<G>, C extends Comparable<? super C>>
        extends Recombinator<G, C> {
    int isActiveOffset = 5;


    public SingleBuySellCrossover(final double probability) {
        super(probability, 2);
    }


    public SingleBuySellCrossover() {
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

        int chromosomeIndex1 = RandomRegistry.getRandom().nextBoolean() ? indexForBuyComparator : indexForSellComparator;
        int chromosomeIndex2 = RandomRegistry.getRandom().nextBoolean() ? indexForBuyComparator : indexForSellComparator;

        int geneIndex1 = RandomRegistry.getRandom().nextInt(gt1.get(chromosomeIndex1).length());
        int geneIndex2 = RandomRegistry.getRandom().nextInt(gt2.get(chromosomeIndex2).length());

        final MSeq<Chromosome<G>> c1 = gt1.toSeq().copy();
        final MSeq<Chromosome<G>> c2 = gt1.toSeq().copy();
        for (int i = -2; i <= 3; i++) {
            c1.get(chromosomeIndex1 + i).toSeq().copy().set(geneIndex1, gt2.get(chromosomeIndex2 + i, geneIndex2));
            c2.get(chromosomeIndex2 + i).toSeq().copy().set(geneIndex2, gt1.get(chromosomeIndex1 + i, geneIndex1));
        }

        c1.get(chromosomeIndex1 + 5).toSeq().copy().set(geneIndex1, gt2.get(chromosomeIndex2 + 5, geneIndex2));
        c2.get(chromosomeIndex2 + 5).toSeq().copy().set(geneIndex2, gt1.get(chromosomeIndex1 + 5, geneIndex1));

        population.set(individuals[0], Phenotype.of(Genotype.of(c1), generation));
        population.set(individuals[1], Phenotype.of(Genotype.of(c1), generation));

        return 5 * 2;
    }


}
