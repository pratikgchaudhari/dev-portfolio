---
title: Top 10 Java HashMap Interview Questions
date: 2025-09-05
summary: Interview questions on HashMap internals, hashing, collisions, performance, and concurrency.
tag: Java
draft: false
---

- **What is a HashMap in Java?**
    - A HashMap is a part of Java's Collections Framework that stores key-value pairs, allowing fast retrieval using keys. It implements the Map interface and uses a hash table for storage.

- **How does HashMap work internally?**
    - HashMap uses a hash table with an array of buckets. Keys are hashed using hashCode(), and the resulting index determines the bucket. Collisions are resolved using linked lists or, in Java 8+, balanced trees for better performance.

- **What is the time complexity of HashMap operations?**
    - Get and put operations are O(1) on average, assuming a good hash function. In worst-case scenarios (many collisions), it can degrade to O(n) for linked lists or O(log n) for trees (Java 8+).

- **What happens when two keys have the same hash code in a HashMap?**
    - If two keys have the same hash code, they are placed in the same bucket. Collisions are resolved by storing entries in a linked list or tree, and equals() is used to differentiate keys.

- **Is HashMap thread-safe?**
    - No, HashMap is not thread-safe. For thread-safe operations, use ConcurrentHashMap or wrap HashMap with Collections.synchronizedMap().

- **What is the difference between HashMap and ConcurrentHashMap?**
    - HashMap is not thread-safe and allows null keys/values. ConcurrentHashMap is thread-safe, supports concurrent access, and does not allow null keys/values.

- **Can HashMap store null keys or values?**
    - Yes, HashMap allows one null key and multiple null values.

- **What is the load factor and initial capacity in HashMap?**
    - Initial capacity is the number of buckets (default 16). Load factor is the threshold (default 0.75) at which the HashMap resizes (doubles) when the number of entries exceeds capacity * load factor.

- **How does HashMap handle resizing?**
    - When the number of entries exceeds the load factor threshold, HashMap doubles its bucket array size and rehashes all entries to redistribute them into new buckets.

- **What is the importance of hashCode() and equals() in HashMap?**
    - hashCode() determines the bucket where the key is stored. equals() is used to compare keys within the same bucket to handle collisions and ensure accurate key retrieval.