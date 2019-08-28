package de.wieczorek.chart.advisor.types;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.nd4j.jita.conf.CudaEnvironment;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wieczorek.rss.core.timer.RecurrentTask;

@RecurrentTask(interval = 30, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTimer.class);

    @Inject
    private TradingNeuralNetworkTrainer network;

    @Inject
	private TrainingDataGenerator generator;

    public TrainingTimer() {

    }

    @Override
    public void run() {
	try {

		CudaEnvironment.getInstance().getConfiguration()
				.setMaximumDeviceCacheableLength(1024 * 1024 * 1024L)
				.setMaximumDeviceCache(10L * 1024 * 1024 * 1024L)
				.setMaximumHostCache(0 * 1024 * 1024 * 1024L);
		Nd4j.setNumThreads(32);


			network.train(generator, 200);


	} catch (Exception e) {
	    logger.error("error while training network: ", e);
	}
    }



}
