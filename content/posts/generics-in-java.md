---
title: Generics in Java
date: 2025-09-26
summary: A guide to type parameters, reusable APIs, and the type-safety benefits of Java generics.
tag: Java
draft: false
---

- Generics in Java, introduced in Java 5, allow type parameters for classes, interfaces, and methods using angle brackets (e.g., `List<String>`) for type safety and reduced casting.
- Provide type safety, eliminate explicit casts, and enable reusable code for any reference type (not primitives, use wrappers like `Integer`).
- Generic classes (e.g., `class Box<T>`) and methods (e.g., `<T> void print(T item)`) use placeholders like `T`, `E`, `K`, `V`.
- Bounded type parameters (e.g., `<T extends Number>`) restrict types to subclasses of a specified type or interface.
- Wildcards include unbounded (`?`), upper-bounded (`? extends Type`), and lower-bounded (`? super Type`) for flexible type handling.
- Type erasure removes generic type info at runtime, resulting in raw types (e.g., `List`).
- Common in collections (e.g., `ArrayList<String>`) and utility methods; cannot instantiate type parameters or use them in static members.
- Best practices: use specific types, avoid raw types, and leverage bounded wildcards for flexibility.