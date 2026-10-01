---
title: Why one should prefer class hierarchies over tagged classes in Java?
date: 2026-01-17
summary: How class hierarchies can replace tag fields and scattered conditional logic with polymorphism.
tag: Java
draft: false
---

- Tagged classes use a tag field (enum/int) to switch behavior
- They include conditional fields (many unused per instance)
- Methods rely on switch/case logic scattered throughout
- Problems: verbose boilerplate, memory waste from unused fields
- Error-prone: easy to miss cases in switches → runtime bugs
- Hard to maintain/extend: adding variants touches all methods
- No compile-time safety or polymorphism
- Class hierarchy fix: abstract base class with concrete subclasses
- Each subclass has only relevant fields and methods
- Behavior handled via overridden polymorphic methods
- Benefits: concise, type-safe, extensible without modifying existing code
- Efficient: no wasted fields or memory
- Readable and scalable: clear intent, easy to add shared behavior
- Embodies OOP "tell, don't ask" principle
- Tagged classes are a procedural relic — avoid in new Java code