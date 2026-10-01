---
title: Top 10 Java Stream Methods
date: 2025-09-01
summary: A reference to common Java Stream operations with descriptions and example use cases.
tag: Java
draft: false
---

| **Method**         | **Description**                                                                 | **Use Case**                                                                                   |
|--------------------|--------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------|
| `filter()`         | Filters elements based on a predicate.                                         | Select elements matching a condition, e.g., filter users by age (`stream.filter(user -> user.getAge() > 18)`). |
| `map()`            | Transforms each element using a function.                                      | Convert elements, e.g., extract names from a list of users (`stream.map(User::getName)`).       |
| `forEach()`        | Performs an action for each element.                                           | Execute a side-effect, e.g., print each element (`stream.forEach(System.out::println)`).         |
| `collect()`        | Accumulates elements into a collection or other result.                        | Gather results into a list/set, e.g., `stream.collect(Collectors.toList())`.                    |
| `sorted()`         | Sorts elements (natural order or custom comparator).                           | Order elements, e.g., sort strings alphabetically (`stream.sorted()`).                          |
| `distinct()`       | Removes duplicates based on `equals()`.                                        | Get unique elements, e.g., remove duplicate names (`stream.distinct()`).                        |
| `limit()`          | Restricts the stream to the first `n` elements.                                | Process only a subset, e.g., get first 5 elements (`stream.limit(5)`).                          |
| `skip()`           | Skips the first `n` elements.                                                  | Ignore initial elements, e.g., skip first 3 elements (`stream.skip(3)`).                        |
| `reduce()`         | Combines elements into a single result using an accumulator.                   | Compute aggregate values, e.g., sum numbers (`stream.reduce(0, Integer::sum)`).                 |
| `flatMap()`        | Flattens nested collections into a single stream.                              | Process nested lists, e.g., flatten list of lists (`stream.flatMap(List::stream)`).             |