package com.kowshik;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompletebleFutureDemo {

    private static final Logger log = LoggerFactory.getLogger(CompletebleFutureDemo.class);

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Create our OWN dedicated thread pool
        ExecutorService myCustomExecutor = Executors.newFixedThreadPool(4);
        System.out.println("Created a custom executor with 4 threads.");

        // Version 0: The Old Way (Future)
        // Limitation: It blocks the main thread until the result is available.
        System.out.println("\n--- Future Demo ---");
        Future<String> future = myCustomExecutor.submit(() -> {
            try {
                Thread.sleep(5000); // Simulate some work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Future Task running in thread: " + Thread.currentThread().getName();
        });

        System.out.println("Future submitted. Main thread doing other work...");
        // This line BLOCKS until the result is ready.
        // We cannot chain actions or handle exceptions gracefully without try-catch
        // here.
        String result = future.get();
        System.out.println("Future Result: " + result);
        System.out.println("--- End Future Demo ---\n");

        // Version 1: Using the default common pool
        CompletableFuture.supplyAsync(() -> {
            // This task will run in the ForkJoinPool.commonPool()
            return "Task running in thread: " + Thread.currentThread().getName();
        }).thenAccept(System.out::println);

        // Version 2: Providing OUR executor (The better way for serious apps)
        CompletableFuture.supplyAsync(() -> {
            // This task is guaranteed to run in OUR executor
            return "Task running in thread: " + Thread.currentThread().getName();
        }, myCustomExecutor).thenAccept(System.out::println); // Pass the executor here

        // Clean up our custom executor
        myCustomExecutor.shutdown();
        myCustomExecutor.awaitTermination(2, TimeUnit.SECONDS);
    }
}
