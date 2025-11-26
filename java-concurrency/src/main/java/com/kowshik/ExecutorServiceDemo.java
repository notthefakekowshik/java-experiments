package com.kowshik;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ExecutorService Demo - Thread Pool Management Tutorial
 *
 * INTERVIEW PREP - Key Topics:
 * =============================
 * 1. Why use ExecutorService instead of creating raw threads?
 *    - Thread reuse and pool management
 *    - Reduced overhead of thread creation/destruction
 *    - Better resource management and control
 *    - Queue-based task submission
 *
 * 2. Common Interview Questions:
 *    - Difference between execute() and submit()?
 *    - Types of thread pools: FixedThreadPool, CachedThreadPool, SingleThreadExecutor, ScheduledThreadPool
 *    - What happens when you submit more tasks than pool size?
 *    - Explain shutdown() vs shutdownNow()
 *    - What is the difference between Runnable and Callable?
 *    - How to handle rejected execution?
 *    - What is ThreadPoolExecutor and its core parameters?
 *
 * 3. Key Differences:
 *    execute(): From Executor interface, returns void, accepts only Runnable
 *    submit(): From ExecutorService, returns Future<?>, accepts Runnable or Callable
 *
 * 4. Thread Pool Types:
 *    - newFixedThreadPool(n): Fixed number of threads, unbounded queue
 *    - newCachedThreadPool(): Creates threads as needed, reuses idle threads
 *    - newSingleThreadExecutor(): Single worker thread, sequential execution
 *    - newScheduledThreadPool(n): For delayed/periodic task execution
 *
 * 5. Best Practices:
 *    - Always shutdown executor service
 *    - Use awaitTermination() to wait for tasks to complete
 *    - Handle InterruptedException properly
 *    - Consider custom ThreadPoolExecutor for fine-grained control
 *
 * 6. Common Pitfalls:
 *    - Forgetting to shutdown executor (app won't terminate)
 *    - Not handling exceptions in submitted tasks
 *    - Using unbounded queues leading to OOM
 */
public class ExecutorServiceDemo {

    /**
     * Demonstrates the benefits of using ExecutorService with a thread pool
     * compared to creating individual threads for each task.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // 1. Create a thread pool with a fixed number of threads (e.g., 10)
        // The JVM doesn't have to create and destroy threads constantly.
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // 2. Submit tasks to the executor service
        for (int i = 0; i < 100; i++) {
            final int taskId = i;
            /*
               execute() comes executor interface
               submit() comes executorService class

               execute() returns nothing and takes only Runnable as an input.
               submit() returns Future and takes Runnable or Callable as an input.
            */
            executor.execute(() -> {
                System.out.println("Executing task " + taskId + " in thread: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });


            // submit() takes a Runnable and adds it to a queue.
            // An available thread from the pool will pick it up and execute it.
            executor.submit(() -> {
                System.out.println("Executing task " + taskId + " in thread: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 3. Shut down the executor service gracefully
        // This is crucial! Otherwise, your application will not terminate.
        System.out.println("All tasks submitted. Shutting down executor.");
        executor.shutdown(); // Disables new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("All tasks completed.");
    }
}


/**
 * ANTI-PATTERN: Do not do this in a real application!
 *
 * This example demonstrates why creating a new thread for every task is inefficient:
 * - Creates 1000 threads, overwhelming the system
 * - High memory overhead (each thread ~1MB stack space)
 * - Context switching overhead degrades performance
 * - No control over thread lifecycle
 */
class RawThreadExample {
    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            final int taskId = i;
            // Create a new thread for every single task - BAD PRACTICE!
            Thread thread = new Thread(() -> {
                System.out.println("Executing task " + taskId + " in thread: " + Thread.currentThread().getName());
                // Simulate work
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            thread.start();
        }
    }
}

/**
 * Runnable vs. Callable - Important Interview Concept:
 *
 * Runnable:
 * - run() method returns void
 * - Cannot throw checked exceptions
 * - Use when task doesn't need to return a result
 *
 * Callable<V>:
 * - call() method returns a value of type V
 * - Can throw checked exceptions
 * - Use when task needs to compute and return a result
 * - Works with Future to get the result asynchronously
 *
 * Example:
 * Callable<Integer> task = () -> {
 *     return 42; // Returns a result
 * };
 * Future<Integer> future = executor.submit(task);
 * Integer result = future.get(); // Blocks until result is available
 */