---
title: Why one should favour static member classes over non-static ones?
date: 2026-01-11
summary: Why static nested classes can reduce hidden references, memory overhead, and accidental retention.
tag: Java
draft: false
---

- Static member classes have no implicit reference to the enclosing instance
- Nonstatic (inner) classes hold a hidden reference to the outer instance
- This hidden reference adds memory overhead (typically 8+ bytes per instance)
- It also slows construction (JVM must initialize the reference)
- Biggest risk: memory leaks if inner instances outlive the outer object
- Common leak scenario: callbacks, listeners, or long-lived collections holding inner instances
- Use nonstatic only when the nested class needs frequent access to outer instance state
- Static classes are semantically cleaner: no dependency on a specific outer instance
- Instantiation is simpler: no qualifying outer instance required
- JDK examples: HashMap.Entry and ArrayList.Itr are static
- Default to static for helpers, iterators, comparators, builders
- Reserve nonstatic for rare cases requiring tight coupling to a specific outer object
- Following this prevents subtle bugs, improves efficiency, and yields better designs