---
title: Reflection APIs vs MethodHandles API in Java
date: 2026-01-11
summary: Compare Java reflection and method handles for dynamic access to methods and fields.
tag: Java
draft: false
---

| Feature              | java.lang.reflect (Reflection)                                                              | java.lang.invoke (MethodHandles)                                                                  |
|----------------------|---------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------|
| **Introduced**       | Java 1.1 (1997)                                                                             | Java 7 (2011, via invokedynamic)                                                                  |
| **Package**          | `java.lang.reflect`                                                                         | `java.lang.invoke`                                                                                |
| **Performance**      | Slower: involves security checks, boxing/unboxing, and cannot be fully inlined by JIT       | Faster: lightweight, JIT can inline invocations after warmup, minimal overhead                    |
| **Type Safety**      | Weak: relies on runtime checks; generic erasure issues                                      | Strong: typed at creation (MethodType); `invokeExact` enforces exact match                        |
| **Access Control**   | Can bypass with `setAccessible(true)` (though suppressed in modules by default)             | Caller-sensitive and module-encapsulated; respects strong encapsulation (Java 9+)                 |
| **Invocation**       | `Method.invoke()` wraps exceptions in `InvocationTargetException`; always boxes primitives  | `invokeExact()` (exact types, checked Throwable) or `invoke()` (type adaptation); less boxing     |
| **Flexibility**      | Limited adapters; mainly for basic introspection and proxies                                | Powerful combinators (`asType`, `guardWithTest`, `filterReturnValue`, `dropArguments`, etc.)      |
| **Ease of Use**      | Simpler API for basic tasks (e.g., `Class.forName`, `getMethod`)                            | Steeper learning curve but more expressive for advanced scenarios                                 |
| **Optimization**     | Limited JVM optimization                                                                    | Excellent: supports monomorphic inlining, used internally for lambdas                             |
| **Common Use Cases** | General introspection, frameworks (Spring, Hibernate), annotation processing, basic proxies | Lambdas/method references (Java 8+), high-performance dynamic code, VarHandles, dynamic languages |
| **Drawbacks**        | Security risks, performance cost, discouraged for new high-performance code                 | More verbose, requires understanding of MethodType and Lookup contexts                            |
| **Recommended For**  | Simple reflection needs, legacy code                                                        | Modern, performance-critical, or complex dynamic invocation                                       