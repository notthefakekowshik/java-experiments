package com.kowshik;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceDemo {
    public static void main(String[] args) {
        // 1. Create a thread pool with a fixed number of threads (e.g., 10)
        // The JVM doesn't have to create and destroy threads constantly.
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // 2. Submit tasks to the executor service
        for (int i = 0; i < 1000; i++) {
            final int taskId = i;
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


// Anti-pattern: Do not do this in a real application!
class RawThreadExample {
    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            final int taskId = i;
            // Create a new thread for every single task
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

/*
Runnable vs. Callable:

Runnable: Its run() method is void and cannot throw checked exceptions. You use it when you don't need a task to return a result.

Callable<V>: Its call() method returns a value of type V and can throw exceptions. This is what you use when your task needs to compute something and return it.
 */