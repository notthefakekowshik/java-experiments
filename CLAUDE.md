# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build all modules
mvn clean install

# Build a specific module
mvn -pl java-concurrency clean install
mvn -pl vanilla-java clean install

# Run all tests
mvn clean test

# Run a specific test class
mvn test -Dtest=TestObjectMapper

# Run a specific test method
mvn test -Dtest=TestObjectMapper#testJacksonMapNullBehavior
```

## Project Structure

This is a Maven multi-module project (`com.kowshik:java-experiments`, Java 21) with two modules:

### `java-concurrency`
Runnable demonstration classes (each with a `main()` method) covering:
- **Synchronization & Locks:** `SynchronizationDemo`, `LockDemo`, `ReentrantLockDemo`, `ReadWriteLockDemo`, `StampedLockDemo`, `ConditionDemo`
- **Atomic & Volatile:** `AtomicVariablesDemo`, `VolatileDemo`
- **Coordination primitives:** `CountDownLatchDemo`, `CyclicBarrierDemo`, `SemaphoreDemo`
- **Executors & async:** `ExecutorServiceDemo`, `CustomThreadPoolDemo`, `ForkJoinPoolDemo`, `CompletebleFutureDemo`
- **Concurrent collections:** `ConcurrentHashMapDemo`, `ConcurrentSkipListMapDemo`, `ConcurrentCacheDemo`
- **Real-world patterns:** `ProducerConsumerDemo`, `ParallelWebCrawlerDemo`, `RateLimiter`, `DataPipelineBarrierDemo`, `DistributedTaskSchedulerDemo`
- **LeetCode-style problems** in `com.leetcodeconcurrency`: `PrintOrder`, `PrintOrderSemaphore`

No JUnit tests — all demos run via `main()`. Uses SLF4J + Logback for logging (configured in `src/main/resources/logback.xml`).

### `vanilla-java`
Core Java concepts with JUnit 5 tests:
- `com.kowshik.lambdas` — functional programming demos
- `com.kowshik.Graphs` — DSU/Union-Find
- `com.kowshik.HEAPS` — custom min-heap
- `com.kowshik.memory` — JVM memory leak simulation
- `tests/` (test source root) — JUnit 5 tests (e.g., `TestObjectMapper` for Jackson behavior)

Dependencies: Jackson 2.15.4, JUnit Jupiter 5.10.0.

## Key Notes

- Concurrency demos are standalone `main()` programs, not unit tests. To run one, execute the class directly from an IDE or with `mvn exec:java -pl java-concurrency -Dexec.mainClass=com.kowshik.XxxDemo`.
- Reference notes for concurrency concepts live in `java-concurrency/src/main/resources/notes.md` and `java-concurrency/CONCURRENCY_TODO.md`.
