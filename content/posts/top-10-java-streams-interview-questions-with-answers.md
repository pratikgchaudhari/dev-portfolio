---
title: Top 10 Java Streams Interview Questions with Answers
date: 2025-09-03
summary: Questions and answers on stream creation, pipelines, intermediate operations, and terminal operations.
tag: Java
draft: false
---

Here are the top 10 Java Stream interview questions with concise, single-line answers:

1. **What is a Java Stream?**  
   A Java Stream is a sequence of elements supporting sequential and parallel aggregate operations.

2. **How do you create a Stream in Java?**  
   Streams can be created using methods like Stream.of(), Collections.stream(), or Arrays.stream().

3. **What is the difference between Stream and Collection?**  
   A Stream is not a data structure but a pipeline for processing data, whereas a Collection stores data.

4. **What are intermediate and terminal operations in Streams?**  
   Intermediate operations (e.g., map, filter) transform a stream; terminal operations (e.g., collect, forEach) produce a result or side-effect.

5. **What is the purpose of the filter() method in Streams?**  
   The filter() method selects elements from a stream based on a predicate.

6. **How does the map() method work in Java Streams?**  
   The map() method transforms each element in a stream using a provided function.

7. **What is lazy evaluation in Java Streams?**  
   Lazy evaluation means intermediate operations are not executed until a terminal operation is invoked.

8. **What is the difference between Stream and ParallelStream?**  
   Stream processes data sequentially, while ParallelStream processes data in parallel using multiple threads.

9. **How do you collect Stream results into a List?**  
   Use stream.collect(Collectors.toList()) to collect stream elements into a List.

10. **What is Optional in the context of Java Streams?**  
    Optional is a container object used to handle potentially null values, often returned by stream operations like findFirst().