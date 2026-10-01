---
title: Top 10 Java 25 Features
date: 2025-09-23
summary: A roundup of Java 25 language and runtime features, from scoped values to constructor changes.
tag: Java
draft: false
---

1. **Scoped Values**: Finalized feature for immutable, thread-local-like data sharing across virtual threads without explicit parameter passing.

2. **Module Import Declarations**: Simplifies imports by allowing `import module M;` to bring in all public classes from a module and its transitive dependencies.

3. **Compact Source Files**: Enables writing simple Java programs without class declarations, ideal for tests and demos.

4. **Instance Main Methods**: Allows defining `main` methods as instance methods (e.g., `void main()` or `void main(String... args)`) instead of requiring `static`, enabling direct instance creation and method calls for simpler program entry points.

5. **Flexible Constructor Bodies**: Permits executable code before `super()` calls, enhancing initialization flexibility.

6. **Generational Shenandoah**: Upgrades Shenandoah GC to generational mode for better throughput and low-latency collection in enterprise apps.

7. **Compact Object Headers**: Reduces object header size to 64 bits on 64-bit JVMs, improving memory efficiency.

8. **JFR CPU-Time Profiling**: Adds Linux kernel-based CPU time sampling to JDK Flight Recorder for precise performance analysis.

9. **PEM Encodings**: Previews API for encoding/decoding cryptographic objects like keys and certificates in PEM format.

10. **Vector API (10th Incubation)**: Continues incubation with enhancements for high-performance vector computations on supported hardware.