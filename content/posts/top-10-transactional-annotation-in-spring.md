---
title: Top 10 Questions of @Transactional annotation in Spring
date: 2025-10-04
summary: Questions and answers on transaction boundaries, propagation, and rollback with Spring's Transactional annotation.
tag: Spring
draft: false
---

1. **What does `@Transactional` do in Spring?**  
   It manages transactions automatically, ensuring data consistency by committing or rolling back operations.

2. **Where can `@Transactional` be applied?**  
   It can be applied to methods or classes, typically in service-layer components.

3. **What is the default propagation behavior of `@Transactional`?**  
   The default propagation is `REQUIRED`, creating a new transaction or joining an existing one.

4. **How does `@Transactional` handle rollbacks?**  
   It rolls back on unchecked exceptions (e.g., `RuntimeException`) by default, unless specified otherwise.

5. **Can `@Transactional` be used with private methods?**  
   No, it works only on public methods due to Spring's proxy-based mechanism.

6. **What is the `readOnly` attribute in `@Transactional`?**  
   `readOnly=true` optimizes for read-only operations, preventing unnecessary transaction overhead.

7. **How does the `isolation` attribute work in `@Transactional`?**  
   It defines transaction isolation levels, like `DEFAULT`, to control data visibility during transactions.

8. **What happens if `@Transactional` is used without a transaction manager?**  
   Spring throws an exception if no transaction manager is configured for the application context.

9. **Can `@Transactional` be nested?**  
   Yes, but nested behavior depends on the `propagation` setting, like `NESTED` or `REQUIRED`.

10. **How does `@Transactional` interact with Spring Boot?**  
    Spring Boot auto-configures a transaction manager, enabling seamless use of `@Transactional`.