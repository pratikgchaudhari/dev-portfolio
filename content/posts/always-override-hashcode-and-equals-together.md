---
title: Why one should always override hashCode() & equals() together?
date: 2026-01-11
summary: How the equals and hashCode contract affects object equality and hash-based collections.
tag: Java
draft: false
---

- Overriding equals without hashCode directly violates the official hashCode contract
- The contract demands: equal objects **must** produce identical hash codes
- Default hashCode is usually memory-address-based → different for distinct instances
- Logically equal objects (by your equals) almost always get different hash codes
- HashMap, HashSet, Hashtable, and all hash-based collections depend on this rule
- Different hash codes → objects land in different buckets → lookup fails
- Result: get() returns null, contains() returns false, Sets allow duplicates
- These bugs are silent, non-deterministic, and extremely difficult to debug
- The rule is absolute: customize equals → you **must** customize hashCode
- Easiest modern fix: `return Objects.hash(field1, field2, …);`
- For better performance on immutable objects: cache the hash code
- Never rely on IDE auto-generation alone — verify it follows the contract
- Breaking this is one of the most common and dangerous mistakes in Java