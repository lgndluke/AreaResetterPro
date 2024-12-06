package com.lgndluke.lgndware.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides functionality for scheduled asynchronous task execution.
 * @author lgndluke
 **/
public class ScheduledAsyncExecutor {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    /**
     * This method will asynchronously execute a RunnableFuture after a specified delay.
     *
     * @param task                  The task to be executed asynchronously.
     * @param delay                 The delay to wait before task execution.
     * @param unit                  The time unit of the delay parameter.
     **/
    private <T> void execute(@NotNull RunnableFuture<T> task, long delay, TimeUnit unit) {
        scheduledExecutorService.schedule(task, delay, unit);
    }

    /**
     * This method will asynchronously execute a RunnableFuture after a specified delay and wait for its completion.
     *
     * @param logger                The Logger instance used for logging.
     * @param task                  The RunnableFuture to be executed.
     * @param delay                 The delay before the task is executed.
     * @param additionalTimeout     The maximum amount of time to wait for execution after the delay.
     * @param unit                  The time unit of the timeout parameters.
     * @return                      True, if the task completed successfully. Otherwise, false.
     **/
    public boolean executeFutureLater(Logger logger, RunnableFuture<Boolean> task, long delay, long additionalTimeout, TimeUnit unit) {
        execute(task, delay, TimeUnit.SECONDS);
        try {
            return task.get(delay+additionalTimeout, unit);
        } catch (TimeoutException te) {
            logger.log(Level.SEVERE, "Scheduled-Task timed out!", te);
            task.cancel(true);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            logger.log(Level.SEVERE, "Scheduled-Task was interrupted!", ie);
        } catch (ExecutionException ee) {
            logger.log(Level.SEVERE, "Scheduled-Task execution failed!", ee);
        }
        return false;
    }

    /**
     * Terminates the Executor.
     **/
    public void shutdown() {
        if(!this.scheduledExecutorService.isShutdown()) {
            this.scheduledExecutorService.shutdown();
        }
    }

}
