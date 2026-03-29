# Java Concurrency Cheatsheet

A quick reference for interview prep and daily development.

## 1. Core Concepts

### Thread Creation
- **Extending Thread**: `class MyThread extends Thread { public void run() { ... } }`
- **Implementing Runnable**: `class MyTask implements Runnable { public void run() { ... } }`
- **Lambda (Java 8+)**: `Thread t = new Thread(() -> { ... });`

### Runnable vs Callable
| Feature | Runnable | Callable |
| :--- | :--- | :--- |
| **Return Value** | `void` | `V` (Generic) |
| **Exception** | Cannot check exception | `throws Exception` |
| **Method** | `run()` | `call()` |
| **Usage** | `new Thread(runnable)` | `executorService.submit(callable)` |

### Thread Lifecycle States
1.  **NEW**: Created but not started.
2.  **RUNNABLE**: Executing in JVM (may depend on OS scheduler).
3.  **BLOCKED**: Waiting for a monitor lock.
4.  **WAITING**: Waiting indefinitely for another thread (e.g., `join()`, `wait()`).
5.  **TIMED_WAITING**: Waiting for up to a specified waiting time.
6.  **TERMINATED**: Execution completed.

---

## 2. Synchronization & Locks

### `synchronized` Keyword
- **Instance Method**: Locks on `this` instance.
- **Static Method**: Locks on the `Class` object.
- **Block**: `synchronized(monitorObj) { ... }` - Preferred for granular control.

### Intrinsic Locks (Monitor)
- Every object has an intrinsic lock and monitor.
- `wait()`: Releases lock, threads enters waiting state.
- `notify()`: Wakes up one waiting thread.
- `notifyAll()`: Wakes up all waiting threads.
- **Rule**: Must be called inside a `synchronized` context.

### `java.util.concurrent.locks`
#### ReentrantLock
- Explicit locking with advanced capabilities (fairness, interruptibility).
- **TryLock**: `if (lock.tryLock()) { ... } else { ... }` prevents blocking.

```java
Lock lock = new ReentrantLock();
lock.lock();
try {
    // critical section
} finally {
    lock.unlock(); // Critical! Always unlock in finally
}
```

#### ReadWriteLock
- Allows multiple readers OR one writer. Good for read-heavy variants.
```java
ReadWriteLock rwLock = new ReentrantReadWriteLock();
// rwLock.readLock().lock();
// rwLock.writeLock().lock();
```

---

## 3. Java Memory Model (JMM)

### `volatile` Keyword
- Guarantees **Visibility**: Changes by one thread are immediately visible to others.
- Prevents **Instruction Reordering**.
- **Does NOT** guarantee atomicity (e.g., `count++` is not safe with just volatile).

### Happens-Before Relationship
- Rules defining partial ordering of operations to guarantee memory visibility.
- Examples:
    - Write to `volatile` variable happens-before subsequent reads.
    - Monitor release (unlock) happens-before subsequent acquire (lock).
    - `Thread.start()` happens-before any action in the started thread.

---

## 4. Concurrent Collections (`java.util.concurrent`)

| Class | Description | Use Case |
| :--- | :--- | :--- |
| **ConcurrentHashMap** | Thread-safe generic map. Uses bucket-level locking (CAS + synchronized). | High concurrency key-value store. No null keys/values. |
| **CopyOnWriteArrayList** | Thread-safe List. Copies array on write. | Read-heavy scenarios with rare updates. |
| **BlockingQueue** | Queue that blocks on put (if full) or take (if empty). | Producer-Consumer patterns. |
| **ConcurrentLinkedQueue** | Non-blocking queue using CAS. | High throughput, non-blocking requirements. |

### Common BlockingQueue Implementations
- `ArrayBlockingQueue`: Bounded, backed by array.
- `LinkedBlockingQueue`: Optionally bounded, linked nodes.
- `PriorityBlockingQueue`: Unbounded, priority heap.
- `SynchronousQueue`: Capacity of 0. Hand-off mechanism.

---

## 5. Executors Framework

### ExecutorService
Decouples task submission from execution.

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
executor.submit(() -> System.out.println("Async Task"));
executor.shutdown();
```

### Common Thread Pools (`Executors` Factory)
1.  **FixedThreadPool(n)**: Fixed number of threads. Unbounded queue.
2.  **CachedThreadPool**: Creates threads as needed, reuses idle ones. Unbounded. Good for short-lived tasks.
3.  **SingleThreadExecutor**: One thread. Sequential execution.
4.  **ScheduledThreadPool**: Scheduling tasks (cron-like).

### Future <V>
Represents result of an asynchronous computation.
- `get()`: Blocks until result is ready.
- `isDone()`: Checks completion.
- `cancel()`: Attempts to cancel execution.

---

## 6. Atomic Variables (`java.util.concurrent.atomic`)
- Lock-free thread-safety using **CAS (Compare-And-Swap)** instructions.
- Classes: `AtomicInteger`, `AtomicLong`, `AtomicBoolean`, `AtomicReference`.

```java
AtomicInteger count = new AtomicInteger(0);
count.incrementAndGet(); // Atomic ++count
```

---

## 7. Modern Concurrency

### CompletableFuture (Java 8)
Composable asynchronous programming. Non-blocking `Future`.
```java
CompletableFuture.supplyAsync(() -> "Hello")
    .thenApply(s -> s + " World")
    .thenAccept(System.out::println)
    .join(); // or get()
```

### Virtual Threads (Java 21+) aka Project Loom
- Lightweight threads managed by JVM (not OS).
- Use blocked standard I/O APIs without blocking OS threads.
- Enables "One Thread per Request" style at scale.

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    IntStream.range(0, 10_000).forEach(i -> {
        executor.submit(() -> {
            Thread.sleep(Duration.ofSeconds(1));
            return i;
        });
    });
}
```

---

## 8. Common Pitfalls & Anti-Patterns
1.  **Deadlock**: Two threads wait for resources held by each other.
    - *Prevention*: Lock ordering, tryLock with timeout.
2.  **Race Condition**: Correctness depends on relative timing of events.
    - *Fix*: Proper synchronization, Atomics.
3.  **Starvation**: Thread unable to gain access to resources indefinitely.
4.  **Double-Checked Locking**: (Singleton) - Needs `volatile` to be safe.
5.  **Thread Leak**: Not shutting down `ExecutorService`.

---

## 9. Interview Quick-fire
- **SemaPhore vs Lock**: Semaphore permits N concurrent access; Lock allows 1.
- **Fail-Fast vs Fail-Safe**:
    - *Fail-Fast*: `ArrayList` throws `ConcurrentModificationException` during iteration if modified.
    - *Fail-Safe*: `ConcurrentHashMap` iterator reflects state at some point; no exception.
- **Context Switch**: Expensive process of storing state of active thread and restoring another. Virtual threads reduce this cost.
