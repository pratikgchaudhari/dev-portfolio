---
title: Why one should prefer try-with-resources over try-finally in Java?
date: 2026-01-13
summary: Simplify resource cleanup and exception handling with Java's try-with-resources statement.
tag: Java
draft: false
---

- Prefer try-with-resources over try-finally for all closeable resources
- Automatically closes resources at end of block — no manual finally needed
- Guarantees closure even when exceptions occur
- Much cleaner, shorter, and more readable code
- Eliminates verbose, nested, error-prone finally blocks
- Correctly handles multiple resources (separated by semicolons)
- Closes resources in reverse order of declaration
- Preserves primary exception from the try block
- Adds any close() exceptions as suppressed exceptions
- Prevents masking/losing of original exceptions (common try-finally bug)
- Works with any type implementing AutoCloseable or Closeable
- Reduces risk of forgetting to close resources
- Makes resource management safer, simpler, and more idiomatic
- Introduced in Java 7 — use it everywhere possible
- Only use try-finally for pre-Java 7 code or non-AutoCloseable cases