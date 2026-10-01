---
title: How does the volatile keyword in Java work?
date: 2025-09-06
summary: Notes on volatile variables, memory visibility, and communication between Java threads.
tag: Java
draft: false
---

The `volatile` keyword in Java ensures all threads "see" the latest changes to a variable by enforcing specific memory visibility rules under the Java Memory Model (JMM).

Main Memory Access: When a variable is declared `volatile`, all reads and writes to it go directly to main memory, bypassing thread-local caches (e.g., CPU registers or cache memory).
Prevents Caching: Threads cannot store a local copy of a `volatile` variable, ensuring every read retrieves the most recent value written by any thread.
Happens-Before Guarantee: The JMM establishes a "happens-before" relationship, where a write to a `volatile` variable by one thread is guaranteed to be visible to any subsequent read by another thread.
Memory Barrier: `volatile` writes insert a store barrier, flushing changes to main memory, while reads insert a load barrier, ensuring fresh data is retrieved, preventing stale reads.
No Locking: Unlike `synchronized`, `volatile` doesn’t use locks, making it lightweight but limited to ensuring visibility, not atomicity for compound operations.

This mechanism ensures all threads see the latest value of a `volatile` variable, making it ideal for simple state flags or single-variable updates across threads.