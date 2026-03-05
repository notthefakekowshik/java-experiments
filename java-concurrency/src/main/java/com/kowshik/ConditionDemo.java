package com.kowshik;


import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionDemo {
    public static void main(String[] args) throws InterruptedException {
        BetterBlockingQueue<Integer> betterBlockingQueue = new BetterBlockingQueue<>(5);

        try (ExecutorService producers = Executors.newFixedThreadPool(10)) {
            producers.submit(() -> {
                for (int i = 0; i < 200; i++) {
                    try {
                        betterBlockingQueue.put(i);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            producers.submit(() -> {
                for (int i = 0; i < 10; i++) {
                    try {
                        betterBlockingQueue.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}

class BetterBlockingQueue<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;

    // 1. The Lock (replacing 'synchronized')
    private final Lock lock = new ReentrantLock();

    // 2. The Conditions (The separate waiting rooms)
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BetterBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void put(T item) throws InterruptedException {
        lock.lock(); // Explicitly acquire the lock
        try {
            // If full, go to the "notFull" waiting room
            while (queue.size() == capacity) {
                System.out.println("!!!! QUEUE IS FULL !!!!!");
                notFull.await();
            }

            queue.add(item);
            System.out.println("PRODUCED : " + item);
            // KEY MOMENT: We ONLY wake up consumers!
            // We signal the 'notEmpty' room.
            // Producers stuck in 'notFull' stay asleep (no contention).
            notEmpty.signal();

        } finally {
            lock.unlock(); // Always unlock in finally
        }
    }

    public T take() throws InterruptedException {
        lock.lock();
        try {
            // If empty, go to the "notEmpty" waiting room
            while (queue.isEmpty()) {
                System.out.println("!!!! QUEUE IS EMPTY !!!!!");
                notEmpty.await();
            }

            T item = queue.remove();
            System.out.println("Consumed : " + item);
            // KEY MOMENT: We ONLY wake up producers!
            // We signal the 'notFull' room.
            notFull.signal();

            return item;
        } finally {
            lock.unlock();
        }
    }
}