---
title: Types of ExecutorService in Java
date: 2025-09-16
summary: Compare Java executor services for fixed, cached, scheduled, and other task workloads.
tag: Java
draft: false
---

1. **FixedThreadPool** (`Executors.newFixedThreadPool(int n)`): Creates a pool with a fixed number of threads. Tasks are queued if all threads are busy. Ideal for consistent workloads.

2. **CachedThreadPool** (`Executors.newCachedThreadPool()`): Creates threads as needed and reuses idle ones. Threads are terminated after 60 seconds of inactivity. Suitable for short-lived tasks.

3. **SingleThreadExecutor** (`Executors.newSingleThreadExecutor()`): Uses a single thread to execute tasks sequentially. Ensures tasks are processed in order. Best for sequential task execution.

4. **ScheduledThreadPool** (`Executors.newScheduledThreadPool(int corePoolSize)`): Supports delayed and periodic task execution. Useful for scheduling tasks to run after a delay or at fixed intervals.

5. **WorkStealingPool** (`Executors.newWorkStealingPool()`): Uses a work-stealing algorithm with multiple threads (default: available processors). Efficient for parallel task execution with uneven workloads.