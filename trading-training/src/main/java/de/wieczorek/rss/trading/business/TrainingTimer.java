package de.wieczorek.rss.trading.business;

import de.wieczorek.rss.core.timer.RecurrentTask;
import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTimer.class);

    public TrainingTimer() {

    }

    private int eval(Genotype<BitGene> gt) {
        return gt.getChromosome()
                .as(BitChromosome.class)
                .bitCount();
    }

    @Override
    public void run() {
        try {

            Factory<Genotype<BitGene>> gtf =
                    Genotype.of(BitChromosome.of(10, 0.5));

            // 3.) Create the execution environment.
            Engine<BitGene, Integer> engine = Engine
                    .builder(this::eval, gtf)
                    .build();

            // 4.) Start the execution (evolution) and
            //     collect the result.
            Genotype<BitGene> result = engine.stream()
                    .limit(100)
                    .collect(EvolutionResult.toBestGenotype());

            System.out.println("Hello World:\n" + result);

        } catch (Exception e) {
            logger.error("error while training network: ", e);
        }
    }
}
