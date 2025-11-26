# Master Guide to Java Multithreading & Concurrency

This guide provides a structured path to understanding and mastering concurrent programming in Java, from fundamental concepts to advanced utilities and modern best practices.

---
## 1. Introduction to Multithreading

### Definition of Multithreading
**Multithreading** is a programming concept where a single process can execute multiple threads concurrently. A **thread** is the smallest unit of execution within a process. It allows a program to perform multiple operations at the same time, improving performance and responsiveness.

**Analogy:** Think of a restaurant. The restaurant is the **process**. The chefs, waiters, and cleaners are the **threads**. They all work simultaneously within the same restaurant, sharing resources like the kitchen and ingredients, to serve customers efficiently.

### Benefits and Challenges
| Benefits | Challenges |
| :--- | :--- |
| **Increased Responsiveness:** Keeps UI applications from freezing. | **Complexity:** Managing threads adds significant complexity. |
| **Enhanced Performance:** Utilizes multiple CPU cores for parallel execution. | **Synchronization Issues:** Race conditions, deadlocks. |
| **Efficient Resource Utilization:** Threads share memory, reducing overhead. | **Context Switching Overhead:** Can degrade performance if overused. |

---
## 2. Processes vs. Threads

A **process** is an instance of a program running in its own memory space. A **thread** is a lightweight component within a process.

| Feature | Process | Thread |
| :--- | :--- | :--- |
| **Memory** | Isolated memory space. | Shares memory space with other threads in the same process. |
| **Communication** | Inter-Process Communication (IPC) is slow (e.g., pipes, sockets). | Inter-Thread Communication is fast (shared memory). |
| **Creation** | Resource-intensive and slow to create. | Lightweight and fast to create. |
| **Fault Isolation** | If one process crashes, it doesn't affect others. | If one thread crashes, it can bring down the entire process. |

### Java Memory Model (JMM)
The JMM defines how threads interact through memory and ensures visibility of changes made by one thread to others.

* **Main Memory:** This is where all shared variables (instance variables, static fields) reside. It's the central resource shared by all threads.
* **Thread Stack:** Each thread has its own private stack. This stack stores local variables, method parameters, and the call stack. Data on the stack is not shared between threads.
* **Heap:** Objects are allocated on the heap, which is part of the main memory and shared among all threads.

---
## 3. Basics of Threads - Part 1: Creation & Lifecycle

### Creating Threads
There are two primary ways to create a thread in Java:

#### 1. Extending the `Thread` Class
You create a class that inherits from `java.lang.Thread` and overrides its `run()` method.

```java
class MyThread extends Thread {
    public void run() {
        System.out.println("Thread running by extending Thread class. ID: " + Thread.currentThread().getId());
    }
}

// To run it:
MyThread t1 = new MyThread();
t1.start(); // Never call run() directly!
```

#### 2. Implementing the `Runnable` Interface
You create a class that implements the `Runnable` interface and its `run()` method. This is the **preferred approach** because it allows your class to extend another class (Java doesn't support multiple inheritance).

```java
class MyRunnable implements Runnable {
    public void run() {
        System.out.println("Thread running by implementing Runnable. ID: " + Thread.currentThread().getId());
    }
}

// To run it:
Thread t2 = new Thread(new MyRunnable());
t2.start();

// Using a Lambda expression (Java 8+)
Runnable myLambda = () -> System.out.println("Thread running from Lambda. ID: " + Thread.currentThread().getId());
Thread t3 = new Thread(myLambda);
t3.start();
```

### Thread Lifecycle
A thread goes through several states during its lifetime:

1.  **NEW:** The thread has been created but has not yet started execution (`t.start()` has not been called).
2.  **RUNNABLE:** The thread is ready to run and is waiting for the CPU scheduler to allocate time. This state includes both "ready" and "running".
3.  **BLOCKED:** The thread is waiting to acquire a monitor lock to enter a `synchronized` block/method.
4.  **WAITING:** The thread is in an indefinite waiting state. It is waiting for another thread to perform a specific action, such as calling `notify()` or `notifyAll()`. This happens after calling `object.wait()`, `thread.join()`, etc.
5.  **TIMED_WAITING:** The thread is waiting for a specific amount of time. This happens after calling `Thread.sleep(ms)`, `object.wait(ms)`, `thread.join(ms)`, etc.
6.  **TERMINATED:** The thread has completed its execution (its `run()` method has finished).

---
## 4. Basics of Threads - Part 2: Synchronization

### Synchronization and Thread Safety
When multiple threads access shared resources (e.g., a variable or object), you need to manage access to prevent data corruption. This is called **thread safety**. **Synchronization** is the mechanism used to achieve it.

A **race condition** occurs when the outcome of a computation depends on the non-deterministic timing of thread execution.

#### Synchronized Methods
Using the `synchronized` keyword on a method ensures that only one thread can execute that method on a given object instance at a time. The lock is on the object (`this`).

```java
class Counter {
    private int count = 0;

    // Only one thread can execute this method at a time on a single Counter instance.
    public synchronized void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}

deentlo flaw ento telsa? what if the increment() is a very huge method? method level blocking is not a good idea.
So, critical code varke block cheyali.
```

#### Synchronized Blocks
This provides more granular control over locking. You can synchronize a specific part of a method instead of the whole thing. You can also lock on different objects.

```java
public void myMethod() {
    // Non-critical section
    System.out.println("Doing some work outside sync block...");

    synchronized(this) {
        // Critical section: only one thread can be in here at a time.
        // ... modify shared resources ...
    }

    // More non-critical work
}
```

### Inter-Thread Communication
Threads can communicate using `wait()`, `notify()`, and `notifyAll()`. These methods are defined in the `Object` class and **must be called from within a synchronized context**.

* `wait()`: Causes the current thread to release the lock and enter the `WAITING` state until another thread calls `notify()` or `notifyAll()` on the same object.
* `notify()`: Wakes up a *single* waiting thread. The choice of which thread is arbitrary.
* `notifyAll()`: Wakes up *all* threads waiting on the object's monitor.

### Producer-Consumer Problem - Assignment
This is a classic concurrency problem.
* **Producer:** A thread that generates data and puts it into a shared buffer.
* **Consumer:** A thread that consumes data from the shared buffer.

**Challenges:**
1.  The producer must not produce data if the buffer is full.
2.  The consumer must not consume data if the buffer is empty.
3.  Access to the shared buffer must be synchronized.

---
## 5. Basics of Threads - Part 3

### Producer-Consumer Problem - Solution
A `BlockingQueue` is the modern, preferred solution. However, a classic solution uses `wait()` and `notifyAll()`.

```java
// A simple implementation using wait() and notifyAll()
class Buffer {
    private Queue<Integer> list;
    private int size;

    public Buffer(int size) {
        this.list = new LinkedList<>();
        this.size = size;
    }

    public void produce(int item) throws InterruptedException {
        synchronized (this) {
            // Wait if the buffer is full
            while (list.size() == size) {
                System.out.println("Buffer is full, producer is waiting...");
                wait();
            }
            list.add(item);
            System.out.println("Produced: " + item);
            // Notify the consumer that an item is available
            notifyAll();
        }
    }

    public int consume() throws InterruptedException {
        synchronized (this) {
            // Wait if the buffer is empty
            while (list.isEmpty()) {
                System.out.println("Buffer is empty, consumer is waiting...");
                wait();
            }
            int item = list.poll();
            System.out.println("Consumed: " + item);
            // Notify the producer that space is available
            notifyAll();
            return item;
        }
    }
}
```
**Why `while` loop instead of `if`?** This prevents "spurious wakeups." A thread might wake up without `notify()` being called. The `while` loop ensures the condition is re-checked before proceeding.

### Deprecated Methods (`stop`, `suspend`, `resume`)
* `stop()`: Is inherently unsafe. It abruptly terminates a thread, releasing all its locks. This can leave shared objects in an inconsistent state.
* `suspend()` & `resume()`: Can easily lead to deadlocks. If a thread is suspended while holding a lock, no other thread can acquire that lock until `resume()` is called.

**Solution:** Use a shared `volatile` boolean flag to signal a thread to stop its work gracefully.

```java
class GracefulStopper implements Runnable {
    private volatile boolean running = true;

    public void stopRunning() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            // do work
            System.out.println("Working...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
            }
        }
        System.out.println("Thread stopped gracefully.");
    }
}
```

### Thread Joining (`join()`)
The `t.join()` method makes the current thread wait until thread `t` has terminated. It's used to ensure a task is complete before the main program proceeds.

```java
Thread worker = new Thread(() -> {
    // Simulate some work
    try { Thread.sleep(2000); } catch (InterruptedException e) {}
    System.out.println("Worker thread finished.");
});

worker.start();
worker.join(); // Main thread will wait here until 'worker' is done.
System.out.println("Main thread continues.");
```

### `volatile` Keyword
The `volatile` keyword ensures that changes to a variable are always visible to other threads. It prevents caching of the variable in a thread's local memory and guarantees that reads/writes happen to/from main memory. It does **not** provide atomicity. Use it for simple flags or status indicators.

### Thread Priority and Daemon Threads
* **Priority:** You can suggest a priority (`Thread.MIN_PRIORITY` to `Thread.MAX_PRIORITY`), but the OS scheduler is free to ignore it. It's not a reliable way to control thread execution.
* **Daemon Threads:** These are low-priority threads that run in the background (e.g., garbage collector). The JVM will exit when only daemon threads are left running. Use `setDaemon(true)` before starting the thread.

---
## 6. Advanced Topics: The `java.util.concurrent` Package

This package provides high-level concurrency utilities that simplify multithreaded programming.

### Thread Pools and the Executor Framework
Creating new threads is expensive. A **thread pool** reuses a fixed number of threads to execute tasks. The **Executor Framework** is the standard API for managing thread pools.

* `Executor`: A simple interface for executing tasks.
* `ExecutorService`: A sub-interface with methods for managing the lifecycle of the pool (e.g., `shutdown()`).
* `ThreadPoolExecutor`: The most common implementation, offering fine-grained control over pool size, queueing, etc.

```java
// Creates a thread pool with a fixed number of 5 threads.
ExecutorService executor = Executors.newFixedThreadPool(5);

for (int i = 0; i < 10; i++) {
    Runnable task = () -> {
        System.out.println("Executing task in thread: " + Thread.currentThread().getName());
    };
    executor.submit(task);
}

executor.shutdown(); // Initiates a graceful shutdown.
```

### `Callable` and `Future`
`Runnable`'s `run()` method doesn't return a value or throw checked exceptions. `Callable` is the alternative.

* `Callable<V>`: An interface similar to `Runnable`, but its `call()` method returns a value of type `V` and can throw exceptions.
* `Future<V>`: Represents the result of an asynchronous computation. When you submit a `Callable` to an `ExecutorService`, you get a `Future` object back. You can use it to check if the task is done, wait for its completion, and retrieve the result.

```java
Callable<Integer> task = () -> {
    TimeUnit.SECONDS.sleep(2);
    return 123;
};

ExecutorService executor = Executors.newSingleThreadExecutor();
Future<Integer> future = executor.submit(task);

System.out.println("Task submitted. Waiting for result...");
Integer result = future.get(); // This blocks until the result is available.
System.out.println("Result is: " + result);

executor.shutdown();
```

### `CompletableFuture` (Java 8+)
A powerful evolution of `Future`. It allows you to chain asynchronous operations, combine multiple futures, and handle errors in a non-blocking, functional style.

```java
CompletableFuture.supplyAsync(() -> "Hello")
    .thenApply(s -> s + " World")
    .thenAccept(System.out::println) // Prints "Hello World"
    .join(); // Wait for the pipeline to complete
```

### Concurrent Collections
These are thread-safe collections that provide higher performance than synchronizing standard collections (e.g., `Collections.synchronizedMap`).

* `ConcurrentHashMap`: A highly scalable, thread-safe `Map`. It uses fine-grained locking (locking on segments or nodes) instead of a single map-wide lock.
* `CopyOnWriteArrayList`: A thread-safe `List` where all mutative operations (add, set, etc.) create a fresh copy of the underlying array. It's excellent for read-heavy workloads but expensive for writes.
* `BlockingQueue`: An interface for queues that block when you try to add to a full queue or retrieve from an empty one. Perfect for producer-consumer scenarios. Implementations include `ArrayBlockingQueue` and `LinkedBlockingQueue`.

### Atomic Variables
Found in `java.util.concurrent.atomic`, these classes support lock-free, thread-safe programming on single variables. They use low-level hardware instructions like **Compare-And-Swap (CAS)** for atomicity, which is often faster than using locks.

* `AtomicInteger`, `AtomicLong`, `AtomicBoolean`

```java
AtomicInteger atomicInt = new AtomicInteger(0);

// Atomically increments the value by 1
atomicInt.incrementAndGet();

// Atomically adds 5
atomicInt.addAndGet(5);
```

### Locks and Semaphores
These provide more advanced and flexible locking mechanisms than the `synchronized` keyword.

* `ReentrantLock`: A lock that a thread can acquire multiple times. It offers features like fairness policies, timed lock attempts, and interruptible lock acquisition. You **must** manually `unlock()` it in a `finally` block.
* `ReadWriteLock`: A lock with two modes: a shared read lock and an exclusive write lock. Multiple threads can hold the read lock simultaneously, but only one thread can hold the write lock. Ideal for data structures that are read far more often than they are modified.
* `Semaphore`: Maintains a set of permits. `acquire()` blocks until a permit is available, and `release()` adds a permit back. It can be used to limit the number of threads accessing a specific resource.

---
## 7. Best Practices, Patterns, and Common Issues

### Concurrency Issues
* **Deadlock:** Two or more threads are blocked forever, each waiting for a resource held by the other.
* **Starvation:** A thread is perpetually denied access to a resource it needs to make progress because other "greedy" threads are monopolizing it.
* **Race Condition:** The correctness of the program depends on the relative timing or interleaving of multiple threads.
* **Livelock:** Threads are actively running but are unable to make progress because they are continuously reacting to each other's state changes.

### Strategies for Avoiding Concurrency Issues
1.  **Immutability:** Make objects immutable. If an object's state cannot be changed after creation, it is inherently thread-safe. `String` is a classic example.
2.  **Minimize Shared State:** The less mutable state you share between threads, the fewer concurrency issues you will have.
3.  **Use High-Level Concurrency Utilities:** Prefer `ExecutorService`, `ConcurrentHashMap`, and `AtomicInteger` over manual locking with `synchronized` and `wait`/`notify`.
4.  **Thread Confinement:** Keep objects confined to a single thread. `ThreadLocal` is a utility that allows you to store data that is local to a specific thread.
5.  **Locking Discipline:** If you must use locks, acquire them in a consistent, fixed order to avoid deadlocks. Keep lock-holding times as short as possible.

### The Double-Checked Locking Issue
A broken pattern for lazy initialization. The `volatile` keyword is essential to make it work correctly in modern Java.

```java
class Singleton {
    // The volatile keyword is crucial here!
    private static volatile Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) { // First check (not synchronized)
            synchronized (Singleton.class) {
                if (instance == null) { // Second check (synchronized)
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```
Without `volatile`, a thread might see a partially constructed `Singleton` object due to instruction reordering by the compiler or CPU.
