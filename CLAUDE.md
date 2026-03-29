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
Fully phased concurrency prep repo. See `java-concurrency/ROADMAP.md` for the learning path and `INSTRUCTIONS.md` for navigation. All demos run via `main()` — no JUnit tests. Uses SLF4J + Logback.

Package structure under `com.kowshik`:

| Package | Contents |
|---|---|
| `threads/` | `ThreadCreationDemo`, `ThreadLifecycleDemo`, `ThreadLocalDemo`, `Threads_Theory.md` |
| `synchronization/` | `SynchronizationDemo`, `VolatileDemo`, `ConditionDemo`, `DeadlockDemo`, `Synchronization_Theory.md` |
| `locks/` | `LockDemo`, `ReentrantLockDemo`, `ReadWriteLockDemo`, `StampedLockDemo`, `Locks_Theory.md` |
| `atomics/` | `AtomicVariablesDemo`, `CASDemo`, `Atomics_Theory.md` |
| `coordination/` | `CountDownLatchDemo`, `CyclicBarrierDemo`, `SemaphoreDemo`, `PhaserDemo`, `ExchangerDemo`, `Coordination_Theory.md` |
| `executors/` | `ExecutorServiceDemo`, `CustomThreadPoolDemo`, `ForkJoinPoolDemo`, `ThreadFactoryDemo`, `Executors_Theory.md`, `BlockingQueue_Theory.md` |
| `future/` | `CompletebleFutureDemo`, `CompletableFutureRunAsyncPrac`, `CompletableFutureSupplyAsyncPrac`, `Future_CompletableFuture_Theory.md` |
| `collections/` | `ConcurrentHashMapDemo`, `ConcurrentSkipListMapDemo`, `Collections_Theory.md` |
| `patterns/` | `ProducerConsumerDemo`, `RateLimiter`, `ConcurrentCacheDemo`, `DataPipelineBarrierDemo`, `Patterns_Theory.md` |
| `advanced/` | `VirtualThreadsDemo`, `ParallelWebCrawlerDemo`, `DistributedTaskSchedulerDemo`, `Advanced_Theory.md` |
| `com.leetcodeconcurrency` | LeetCode problems organized under `Design*/` sub-packages |

### `vanilla-java`
Core Java concepts with JUnit 5 tests:
- `com.kowshik.lambdas` — functional programming demos
- `com.kowshik.Graphs` — DSU/Union-Find
- `com.kowshik.HEAPS` — custom min-heap
- `com.kowshik.memory` — JVM memory leak simulation
- `tests/` (test source root) — JUnit 5 tests (e.g., `TestObjectMapper` for Jackson behavior)

Dependencies: Jackson 2.15.4, JUnit Jupiter 5.10.0.

## Key Notes

- Concurrency demos are standalone `main()` programs. Run with `mvn exec:java -pl java-concurrency -Dexec.mainClass=com.kowshik.threads.ThreadCreationDemo` (adjust package + class as needed).
- Theory for each topic lives in its package `*_Theory.md`. Master roadmap: `java-concurrency/ROADMAP.md`.
