---
title: Reentrant Lock in Java:
date: 2025-09-04
summary: A reference to explicit locking, fairness, timed acquisition, and conditions with ReentrantLock.
tag: Java
draft: false
---

- **Reentrant**: Thread can reacquire lock, incrementing hold count.
- **Explicit Lock/Unlock**: Uses `lock()` and `unlock()`, typically in try-finally.
- **Fairness**: Optional fair mode (`new ReentrantLock(true)`) reduces starvation.
- **TryLock**: Non-blocking `tryLock()` or timed `tryLock(long, TimeUnit)`.
- **Conditions**: Supports multiple `Condition` objects for thread coordination.
- **Interruptible**: `lockInterruptibly()` allows interruption during lock wait.
- **Hold Count**: Tracks reentrant acquisitions with `getHoldCount()`.
- **Ownership**: Checks lock owner (`getOwner()`) or current thread (`isHeldByCurrentThread()`).
- **Queue Info**: Inspects waiting threads with `getQueueLength()` or `getWaitingThreads()`.
- **Flexible Scope**: Lock/unlock not bound to block scope.
- **High Performance**: Often outperforms `synchronized` in contention.
- **No Deadlock Prevention**: Requires careful use to avoid deadlocks.