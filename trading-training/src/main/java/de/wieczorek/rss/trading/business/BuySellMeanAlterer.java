package de.wieczorek.rss.trading.business;

import io.jenetics.*;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.Mean;
import io.jenetics.util.RandomRegistry;

import java.util.Random;

public class BuySellMeanAlterer<G extends Gene<Integer, G> & Mean<G>, C extends Comparable<? super C>>
        extends Recombinator<G, C> {
    int isActiveOffset = 5;

    public BuySellMeanAlterer(final double probability) {
        super(probability, 2);
    }


    public BuySellMeanAlterer() {
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

        Chromosome<G> chromosome = gt1.get(chromosomeIndex);
        int gt1Index = random.nextInt(chromosome.length());
        int buySellType = chromosome.getGene(random.nextInt(chromosome.length())).getAllele();

        int index = -1;
        int counter = 0;
        for (Gene<Integer, G> gene : gt2.get(chromosomeIndex)) {
            if (gene.getAllele().equals(buySellType) && gt1.get(chromosomeIndex + isActiveOffset, gt1Index).getAllele() == 1) {
                index = counter;
                break;
            }
            counter++;
        }

        MSeq<Chromosome<G>> newChromosomes;

        int alteredGenes = 0;
        if (index != -1) {
            newChromosomes = calculateMean(gt1, gt2, chromosomeIndex, gt1Index, index);
            alteredGenes = 2;
        } else {
            newChromosomes = add(gt1, gt2, chromosomeIndex);
            alteredGenes = 6;
        }

        population.set(individuals[1], Phenotype.of(Genotype.of(newChromosomes), generation));

        return alteredGenes;
    }

    private MSeq<Chromosome<G>> add(Genotype<G> gt1, Genotype<G> gt2, int chromosomeIndex) {
        int index = -1;
        int counter = 0;
        for (Gene<Integer, G> gene : gt2.get(chromosomeIndex + isActiveOffset)) {
            if (gene.getAllele() == 0) {
                index = counter;
                break;
            }
            counter++;
        }

        if (index == -1) {
            return gt1.toSeq().copy();
        }

        final int thresholdIndex = chromosomeIndex - 2;
        final int averagingDurationIndex = chromosomeIndex - 1;
        final int comparatorIndex = chromosomeIndex;
        final int offsetIndex = chromosomeIndex + 1;
        final int averageTypeIndex = chromosomeIndex + 2;
        final int valueSourceIndexIndex = chromosomeIndex + 3;
        final int isActiveIndex = chromosomeIndex + 5;


        final MSeq<Chromosome<G>> c2 = gt2.toSeq().copy();

        c2.get(thresholdIndex).toSeq().copy().set(index, gt1.get(thresholdIndex, index));
        c2.get(averagingDurationIndex).toSeq().copy().set(index, gt1.get(averagingDurationIndex, index));
        c2.get(comparatorIndex).toSeq().copy().set(index, gt1.get(comparatorIndex, index));
        c2.get(offsetIndex).toSeq().copy().set(index, gt1.get(offsetIndex, index));
        c2.get(averageTypeIndex).toSeq().copy().set(index, gt1.get(averageTypeIndex, index));
        c2.get(valueSourceIndexIndex).toSeq().copy().set(index, gt1.get(averagingDurationIndex, index));
        c2.get(isActiveIndex).toSeq().copy().set(index, gt1.get(isActiveIndex, index));

        return c2;
    }

    private MSeq<Chromosome<G>> calculateMean(Genotype<G> gt1, Genotype<G> gt2, int chromosomeIndex, int index1, int index2) {
        final ISeq<Chromosome<G>> c1 = gt1.toSeq();
        final MSeq<Chromosome<G>> c2 = gt2.toSeq().copy();

        final int thresholdIndex = chromosomeIndex - 2;
        final int averagingDurationIndex = chromosomeIndex - 1;

        ISeq<G> threshold1 = c1.get(thresholdIndex).toSeq();
        MSeq<G> threshold2 = c2.get(thresholdIndex).toSeq().copy();

        threshold2.set(index1, threshold1.get(index1).mean(threshold2.get(index2)));
        c2.set(thresholdIndex, c1.get(thresholdIndex).newInstance(threshold2.toISeq()));

        ISeq<G> averagingDuration1 = c1.get(averagingDurationIndex).toSeq();
        MSeq<G> averagingDuration2 = c2.get(averagingDurationIndex).toSeq().copy();

        averagingDuration2.set(index1, averagingDuration1.get(index1).mean(averagingDuration2.get(index2)));
        c2.set(averagingDurationIndex, c2.get(averagingDurationIndex).newInstance(averagingDuration2.toISeq()));

        return c2;
    }
}
