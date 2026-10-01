---
title: Hashtable vs HashMap in Java
date: 2025-08-31
summary: Compare Hashtable and HashMap by synchronization, performance, null handling, and everyday use.
tag: Java
draft: false
---

| Feature | Hashtable | HashMap |
|---------|-----------|---------|
| **Introduced** | Java 1.0 (Legacy class) | Java 1.2 (Collections Framework) |
| **Synchronization** | Thread-safe (synchronized methods) | Not thread-safe |
| **Performance** | Slower due to synchronization overhead | Faster due to no synchronization |
| **Null Keys** | Does not allow null keys | Allows one null key |
| **Null Values** | Does not allow null values | Allows multiple null values |
| **Inheritance** | Extends `Dictionary` class | Extends `AbstractMap` class |
| **Iterator** | `Enumerator` (not fail-fast) | `Iterator` (fail-fast) |
| **Fail-Fast** | Enumeration is not fail-fast | Iterator throws `ConcurrentModificationException` if modified during iteration |
| **Default Capacity** | 11 | 16 |
| **Load Factor** | 0.75 | 0.75 |
| **Use Case** | Suitable for thread-safe applications | Preferred for non-thread-safe applications |
| **Modern Usage** | Rarely used due to `ConcurrentHashMap` | Widely used due to flexibility and performance |