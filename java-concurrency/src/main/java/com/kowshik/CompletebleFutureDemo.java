package com.kowshik;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CompletableFuture Demo - Asynchronous Programming Tutorial
 *
 * INTERVIEW PREP - Key Topics:
 * =============================
 * 1. What is CompletableFuture and why use it over Future?
 *    - Non-blocking asynchronous programming
 *    - Composable and chainable operations
 *    - Exception handling with exceptionally/handle
 *    - Combining multiple futures (thenCombine, allOf, anyOf)
 *
 * 2. Common Interview Questions:
 *    - Difference between supplyAsync() and runAsync()
 *    - When to provide custom executor vs using common pool?
 *    - How to handle exceptions in CompletableFuture?
 *    - Explain thenApply() vs thenCompose() vs thenCombine()
 *    - What is the difference between thenAccept() and thenApply()?
 *    - How does CompletableFuture avoid callback hell?
 *
 * 3. Real-world Use Cases:
 *    - Microservices orchestration
 *    - Parallel API calls
 *    - Database + cache queries in parallel
 *    - Timeout handling with orTimeout()
 *
 * 4. Common Pitfalls:
 *    - Not shutting down custom executors
 *    - Blocking operations in async chain
 *    - ForkJoinPool.commonPool() starvation
 *    - Not handling exceptions properly
 *
 * Key Methods to Know:
 * - supplyAsync/runAsync: Start async computation
 * - thenApply/thenAccept/thenRun: Transform/consume results
 * - thenCompose: Chain dependent futures
 * - thenCombine: Combine independent futures
 * - exceptionally/handle: Error handling
 * - allOf/anyOf: Multiple futures coordination
 */
public class CompletebleFutureDemo {

    private static final Logger log = LoggerFactory.getLogger(CompletebleFutureDemo.class);

    /**
     * Demonstrates the difference between using ForkJoinPool.commonPool()
     * vs providing a custom executor for CompletableFuture operations.
     *
     * @param args command line arguments
     * @throws InterruptedException if interrupted while waiting
     */
    public static void main(String[] args) throws InterruptedException {
        // Create our OWN dedicated thread pool
        ExecutorService myCustomExecutor = Executors.newFixedThreadPool(4);
        log.info("Created a custom executor with 4 threads");

        // Version 1: Using the default common pool (ForkJoinPool.commonPool())
        CompletableFuture.supplyAsync(() -> {
            // This task will run in the ForkJoinPool.commonPool()
            String threadName = Thread.currentThread().getName();
            log.info("Task 1 running in common pool thread: {}", threadName);
            return "Task running in thread: " + threadName;
        }).thenAccept(result -> log.info("Result 1: {}", result));

        // Version 2: Providing OUR executor (The better way for production apps)
        CompletableFuture.supplyAsync(() -> {
            // This task is guaranteed to run in OUR executor
            String threadName = Thread.currentThread().getName();
            log.info("Task 2 running in custom executor thread: {}", threadName);
            return "Task running in thread: " + threadName;
        }, myCustomExecutor).thenAccept(result -> log.info("Result 2: {}", result));

        // Give tasks time to complete
        Thread.sleep(500);

        // Clean up our custom executor
        log.info("Shutting down custom executor");
        myCustomExecutor.shutdown();
        if (!myCustomExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
            myCustomExecutor.shutdownNow();
        }
        log.info("Executor shut down successfully");
    }
}
