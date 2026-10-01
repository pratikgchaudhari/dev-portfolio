---
title: Classloaders in Java
date: 2026-01-10
summary: How Java class loaders find classes, delegate loading, and organize the runtime class hierarchy.
tag: Java
draft: false
---

- ClassLoaders in Java dynamically load .class files into the JVM at runtime.
- They follow a hierarchical structure with parent-child relationships.
- Bootstrap ClassLoader (native, parent null) loads core JDK classes (e.g., java.lang.*).
- Platform ClassLoader (Java 9+) loads modular platform classes.
- Application ClassLoader loads classes from the application's CLASSPATH.
- ClassLoaders use the parent-first delegation model.
- On a load request, a ClassLoader first delegates to its parent.
- If the parent fails to load the class, the child ClassLoader attempts to load it.
- This ensures core classes are loaded only by trusted loaders.
- The process involves loading (find bytes), linking (verify, prepare, resolve), and initialization.
- Custom ClassLoaders can override loadClass() to change delegation behavior.
- Classes loaded by different ClassLoaders are treated as distinct, even if identical bytecode.
- Namespace isolation prevents class conflicts in modular applications (e.g., web servers).