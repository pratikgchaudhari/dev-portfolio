---
title: Concurrent Collections in Java
date: 2025-09-04
summary: An overview of Java's concurrent maps, sets, queues, and other thread-safe collections.
tag: Java
draft: false
---

- **ConcurrentHashMap**: A thread-safe hash table supporting full concurrency of retrievals and high expected concurrency for updates.
- **ConcurrentSkipListMap**: A thread-safe sorted map using a skip list data structure, allowing concurrent modifications and iterations.
- **ConcurrentSkipListSet**: A thread-safe sorted set backed by a `ConcurrentSkipListMap`, ensuring ordered elements with concurrent access.
- **CopyOnWriteArrayList**: A thread-safe `ArrayList` variant where all mutative operations create a new copy of the underlying array.
- **CopyOnWriteArraySet**: A thread-safe set backed by a `CopyOnWriteArrayList`, suitable for small sets with infrequent modifications.
- **ConcurrentLinkedQueue**: A thread-safe, non-blocking queue based on linked nodes, supporting high-throughput FIFO operations.
- **ConcurrentLinkedDeque**: A thread-safe, non-blocking double-ended queue based on linked nodes, supporting FIFO and LIFO operations.
- **ArrayBlockingQueue**: A bounded, thread-safe queue backed by an array, blocking when full or empty until operations can proceed.
- **LinkedBlockingQueue**: An optionally bounded, thread-safe queue based on linked nodes, blocking when full or empty.
- **LinkedBlockingDeque**: An optionally bounded, thread-safe double-ended queue based on linked nodes, blocking when full or empty.
- **PriorityBlockingQueue**: An unbounded, thread-safe priority queue based on a priority heap, blocking when empty.
- **SynchronousQueue**: A blocking queue with no capacity, where each insert operation must wait for a corresponding remove operation, and vice versa.
- **DelayQueue**: An unbounded blocking queue of elements that are only available for removal after a specified delay.
- **LinkedTransferQueue**: An unbounded queue that supports thread-to-thread item transfers, combining queue and message-passing semantics.