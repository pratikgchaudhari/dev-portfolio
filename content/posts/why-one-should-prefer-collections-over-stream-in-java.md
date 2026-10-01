---
title: Why one should prefer Collections over Stream as a return type?
date: 2026-01-16
summary: Why collections can provide a more flexible return type than single-use streams.
tag: Java
draft: false
---

- Collections allow multiple iterations, size checks, contains, random access
- Streams are single-use — consumed after terminal operation
- Clients cannot easily reuse or for-each a Stream directly
- Returning Stream forces clients to collect into a collection for most uses
- Collections are more versatile, familiar, and client-friendly
- Clients can always call collection.stream() if needed
- For small fixed sequences: use List.of(), Set.of(), Map.of()
- For general cases: return List<E>, Set<E>, or modifiable Collection
- For infinite/lazy sequences: return Iterable<E>
- Arrays acceptable for primitives or performance-critical cases
- Return Stream rarely — only for adapters or inherently single-use sources
- Examples: Files.lines() is acceptable adapter; prefer readAllLines() otherwise
- Goal: maximize API usability and flexibility for clients
- This makes APIs more idiomatic and avoids frustrating boilerplate