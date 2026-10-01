---
title: Top 10 questions for @Query annotation in Spring Data
date: 2025-10-04
summary: Questions and answers on custom JPQL and SQL queries, parameters, and pagination in Spring Data.
tag: Spring
draft: false
---

1. **What is the `@Query` annotation used for in Spring?**  
   Defines custom JPQL or native SQL queries in Spring Data JPA repositories for precise data retrieval or manipulation.

2. **How do you write a JPQL query using `@Query`?**  
   Use `@Query("SELECT e FROM Entity e WHERE e.field = :param")` in a repository method, binding parameters with `@Param`.

3. **Can `@Query` be used for native SQL queries?**  
   Yes, set `nativeQuery = true` in `@Query` to execute native SQL, e.g., `@Query(value = "SELECT * FROM table", nativeQuery = true)`.

4. **How do you pass parameters to `@Query`?**  
   Use `:?` or `@Param("name")`, e.g., `@Query("SELECT e FROM Entity e WHERE e.field = :name")` with method parameter `@Param("name") String value`.

5. **What’s the difference between JPQL and native SQL in `@Query`?**  
   JPQL queries operate on entities and are database-agnostic; native SQL queries are database-specific and use raw SQL.

6. **How do you handle pagination with `@Query`?**  
   Add `Pageable` as a method parameter, e.g., `@Query("SELECT e FROM Entity e") Page<Entity> findAll(Pageable pageable)`.

7. **Can `@Query` be used for update or delete operations?**  
   Yes, with `@Modifying`, e.g., `@Modifying @Query("UPDATE Entity e SET e.field = :value")` for updates.

8. **How do you use `@Query` with `LIKE` in JPQL?**  
   Use `LIKE` in the query, e.g., `@Query("SELECT e FROM Entity e WHERE e.field LIKE %:pattern%")` for pattern matching.

9. **What happens if `@Query` syntax is incorrect?**  
   Spring throws a runtime exception (e.g., `InvalidJpaQueryMethodException`) during application startup or query execution.

10. **Can `@Query` return custom DTOs instead of entities?**  
    Yes, use a constructor expression in JPQL, e.g., `@Query("SELECT new com.example.MyDto(e.field1, e.field2) FROM Entity e")`.