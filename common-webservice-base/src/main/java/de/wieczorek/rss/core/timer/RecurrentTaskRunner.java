package de.wieczorek.rss.core.timer;

import de.wieczorek.rss.core.persistence.EntityManagerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.RollbackException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class RecurrentTaskRunner {

    private static final Logger logger = LoggerFactory.getLogger(RecurrentTaskRunner.class);
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private Runnable task;
    private int interval;
    private TimeUnit unit;

    private ReentrantLock lock = new ReentrantLock();

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
        nextInvocation = executor.schedule(this::run, interval, unit);
        logger.debug("scheduling again in " + interval + " " + unit);
        try {
            if (lock.tryLock()) {
                task.run();
                lock.unlock();
            }
        } catch (RollbackException r) {
            EntityManagerProvider.getEntityManager().clear();
        } catch (Exception e) {
            e.printStackTrace();
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
                logger.error("error stopping thread: ", e);
            }
        }
    }

}
