---
title: Unsafe Class in Java
date: 2026-01-10
summary: An overview of Unsafe's low-level operations and how they bypass Java's usual safety checks.
tag: Java
draft: false
---

The `sun.misc.Unsafe` (or `jdk.internal.misc.Unsafe` in newer JDKs) class in Java provides low-level operations that bypass normal safety checks.

- **Direct memory management** — Allocates, frees, and accesses memory outside the Java heap (e.g., `allocateMemory`, `getLong`, `putInt` at arbitrary addresses).
- **Object instantiation without constructors** — Creates instances of classes directly (e.g., `allocateInstance`), skipping initialization and access checks.
- **Direct field manipulation** — Reads/writes object fields (instance or static) bypassing visibility modifiers and type safety (e.g., via `objectFieldOffset`).
- **Atomic and volatile operations** — Supports compare-and-swap (CAS), load/store fences, and atomic updates for lock-free concurrency.
- **Array manipulation** — Retrieves base offsets and scaling factors for efficient raw array access.
- **Thread control** — Parks/unparks threads (used by `LockSupport`) and monitors thread states.
- **Exception handling** — Forces exceptions to be thrown from any thread.
- **Other low-level utilities** — Includes operations like copying memory, determining object sizes, and accessing JVM internals.

It is intended for JDK internal use and high-performance libraries; misuse can crash the JVM or cause undefined behavior. Access is restricted and typically obtained via reflection.