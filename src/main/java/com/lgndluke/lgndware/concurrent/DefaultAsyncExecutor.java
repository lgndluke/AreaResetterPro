package com.lgndluke.lgndware.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides functionality for asynchronous task execution.
 * @author lgndluke
 **/
public class DefaultAsyncExecutor {

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    /**
     * @param task The task to be executed asynchronously.
     **/
    private <T> void execute(@NotNull RunnableFuture<T> task) {
        executorService.execute(task);
    }

    /**
     * This method will asynchronously execute a RunnableFuture and wait for its completion.
     *
     * @param logger        The Logger instance used for logging.
     * @param task          The RunnableFuture to be executed.
     * @param timeout       The maximum time to wait for execution.
     * @param unit          The time unit of the timeout parameter.
     * @return              True, if the task was completed successfully. Otherwise, false.
     **/
    public boolean executeFuture(Logger logger, RunnableFuture<Boolean> task, long timeout, TimeUnit unit) {
        execute(task);
        try {
            return task.get(timeout, unit);
        } catch (TimeoutException te) {
            logger.log(Level.SEVERE, "Default-Task timed out!", te);
            task.cancel(true);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            logger.log(Level.SEVERE, "Default-Task was interrupted!", ie);
        } catch (ExecutionException ee) {
            logger.log(Level.SEVERE, "Default-Task execution failed!", ee);
        }
        return false;
    }

    /**
     * This method will asynchronously execute a RunnableFuture and wait for its completion, returning the results
     * as a List.
     *
     * @param logger        The Logger of the caller instance.
     * @param task          The RunnableFuture to be executed.
     * @param timeout       The maximum time to wait for execution.
     * @param unit          The time unit of the timeout parameter.
     * @return              The list result of the task execution, or null if execution failed/timed-out.
     **/
    public <T> List<T> fetchExecutionResultAsList(Logger logger, RunnableFuture<List<T>> task, long timeout, TimeUnit unit) {
        execute(task);
        try {
            return task.get(timeout, unit);
        } catch (TimeoutException te) {
            logger.log(Level.SEVERE, "Default-Task timed out.", te);
            task.cancel(true);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            logger.log(Level.SEVERE, "Default-Task was interrupted", ie);
        } catch (ExecutionException ee) {
            logger.log(Level.SEVERE, "Default-Task execution failed", ee);
        }
        return null;
    }

    /**
     * This method will asynchronously execute a RunnableFuture and wait for its completion, returning the result.
     *
     * @param logger        The Logger of the caller instance.
     * @param task          The RunnableFuture to be executed.
     * @param timeout       The maximum time to wait for execution.
     * @param unit          The time unit of the timeout parameter.
     * @return              The result of the task execution, or null if execution failed/timed-out.
     **/
    public <T> T fetchExecutionResult(Logger logger, RunnableFuture<T> task, long timeout, TimeUnit unit) {
        execute(task);
        try {
            return task.get(timeout, unit);
        } catch (TimeoutException te) {
            logger.log(Level.SEVERE, "Default-Task timed out.", te);
            task.cancel(true);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            logger.log(Level.SEVERE, "Default-Task was interrupted", ie);
        } catch (ExecutionException ee) {
            logger.log(Level.SEVERE, "Default-Task execution failed", ee);
        }
        return null;
    }

    /**
     * Terminates this Executor.
     **/
    public void shutdown() {
        if(!this.executorService.isShutdown()) {
            this.executorService.shutdown();
        }
    }

}
