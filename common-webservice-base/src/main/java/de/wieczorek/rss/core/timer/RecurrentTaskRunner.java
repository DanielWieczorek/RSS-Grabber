package de.wieczorek.rss.core.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecurrentTaskRunner {

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private static final Logger logger = LogManager.getLogger(RecurrentTaskRunner.class.getName());

    private Runnable task;
    private int interval;
    private TimeUnit unit;

    private ScheduledFuture<?> nextInvocation;

    RecurrentTaskRunner(Runnable task, int interval, TimeUnit unit) {
	this.task = task;
	this.interval = interval;
	this.unit = unit;
    }

    public void start() {
	stop();
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
	    nextInvocation = executor.schedule(this::run, interval, unit);
	    logger.debug("scheduling again in " + interval + " " + unit);
	}
    }

    public void stop() {
	if (executor != null) {
	    executor.shutdownNow();
	    try {

		executor.shutdown();
		if (nextInvocation != null) {
		    nextInvocation.cancel(false);
		}
		executor.awaitTermination(10, TimeUnit.SECONDS);

	    } catch (InterruptedException e) {
		logger.error("error stopping rss reader: ", e);
	    }
	}
    }

}
