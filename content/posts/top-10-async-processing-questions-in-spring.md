---
title: Top 10 questions related to Async processing in Spring
date: 2025-10-01
summary: Questions and answers on asynchronous methods, executors, scheduling, and error handling in Spring.
tag: Spring
draft: false
---

1. **How do you enable asynchronous processing in Spring?**  
   Use `@EnableAsync` on a configuration class to enable async support.

2. **How do you define an asynchronous method in Spring?**  
   Annotate the method with `@Async` and ensure it returns `void`, `Future`, `CompletableFuture`, or `ListenableFuture`.

3. **How do you configure a thread pool for async tasks in Spring?**  
   Define a `ThreadPoolTaskExecutor` bean and customize its properties like pool size.

4. **What happens if an async method throws an exception?**  
   Exceptions are caught by the async executor; use `AsyncUncaughtExceptionHandler` to handle them.

5. **How do you schedule a recurring task in Spring?**  
   Use `@Scheduled` with attributes like `fixedRate`, `fixedDelay`, or `cron` for scheduling.

6. **What is the difference between `@Async` and `@Scheduled`?**  
   `@Async` runs methods asynchronously on demand, while `@Scheduled` runs tasks on a fixed schedule.

7. **How do you ensure a scheduled task runs only once across a cluster?**  
   Use a distributed lock or a task scheduler like Quartz with database persistence.

8. **Can you call an `@Async` method from the same class?**  
   No, it requires a proxy; call it from another bean or use `ApplicationContext` to get the bean.

9. **How do you monitor the status of async tasks in Spring?**  
   Use `CompletableFuture` or `ListenableFuture` to track task completion or add custom logging.

10. **What is the default executor for `@Async` in Spring?**  
    Spring uses a `SimpleAsyncTaskExecutor`, creating a new thread per task unless customized.