package de.wieczorek.rss.core.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecurrentTaskRunner {

    private ScheduledExecutorService executor;

    private static final Logger logger = LogManager.getLogger(RecurrentTaskRunner.class.getName());

    private Runnable task;
    private int interval;
    private TimeUnit unit;

    RecurrentTaskRunner(Runnable task, int interval, TimeUnit unit) {
	this.task = task;
	this.interval = interval;
	this.unit = unit;
    }

    public void start() {
	if (executor == null || executor.isShutdown()) {
	    executor = Executors.newScheduledThreadPool(1);
	}
	executor.execute(this::run);

    }

    private void run() {
	try {
	    task.run();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    executor.schedule(this::run, interval, unit);
	    logger.debug("scheduling again in " + interval + " " + unit);
	}
    }

    public void stop() {
	executor.shutdownNow();
	try {
	    executor.awaitTermination(2, TimeUnit.MINUTES);
	} catch (InterruptedException e) {
	    logger.error("error stopping rss reader: ", e);
	}
    }

}
