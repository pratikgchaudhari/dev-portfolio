---
title: Runnable vs Callable in Java
date: 2025-09-28
summary: Compare Runnable and Callable by return values, exception handling, and task execution.
tag: Java
draft: false
---

| **Feature**                    | **Runnable**                                                                 | **Callable**                                                               |
|--------------------------------|------------------------------------------------------------------------------|----------------------------------------------------------------------------|
| **Introduced In**              | Java 1.0                                                                     | Java 5 (java.util.concurrent)                                              |
| **Package**                    | java.lang                                                                    | java.util.concurrent                                                       |
| **Functional Interface**       | Yes, has a single method `run()`                                             | Yes, has a single method `call()`                                          |
| **Method Signature**           | `void run()`                                                                 | `V call() throws Exception`                                                |
| **Return Type**                | Does not return any value (void)                                             | Returns a value of type `V` (generic)                                      |
| **Exception Handling**         | Cannot throw checked exceptions; unchecked exceptions can be thrown          | Can throw checked exceptions (e.g., `Exception`) and unchecked exceptions  |
| **Usage with ExecutorService** | Used with `ExecutorService.submit(Runnable)` or `execute(Runnable)`          | Used with `ExecutorService.submit(Callable)` to obtain a `Future` object   |
| **Result Retrieval**           | No direct way to retrieve results; relies on shared variables or other means | Returns a `Future` object to retrieve the result or handle exceptions      |
| **Typical Use Case**           | Suitable for tasks that perform actions without returning results            | Suitable for tasks that compute and return results or may throw exceptions |
| **Example**                    | ```Runnable r = () -> System.out.println("Running");<br>```                  | ```Callable<Integer> c = () -> 42;<br>```                                  |