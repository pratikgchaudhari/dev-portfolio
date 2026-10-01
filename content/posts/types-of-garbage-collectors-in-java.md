---
title: Types of Garbage Collectors in Java
date: 2025-08-30
summary: A reference to Java garbage collectors and their approaches to throughput, pauses, and memory.
tag: Java
draft: false
---

1. **Serial GC**: Single-threaded, stop-the-world collector for small apps or single-core systems.
2. **Parallel GC**: Multi-threaded, throughput-focused collector with stop-the-world pauses.
3. **Concurrent Mark-Sweep (CMS) GC**: Mostly concurrent, low-latency collector, deprecated due to fragmentation.
4. **G1 (Garbage-First) GC**: Region-based, balances throughput and pause time, default since Java 9.
5. **Z Garbage Collector (ZGC)**: Low-latency, concurrent GC for large heaps with minimal pauses.
6. **Shenandoah GC**: Low-latency, concurrent compaction for consistent response times.
7. **Epsilon GC**: No-op GC for testing or short-lived apps, no memory reclamation.