---
title: Why one should override Object.clone() judiciously?
date: 2026-01-15
summary: The pitfalls of Cloneable, shallow copies, mutable state, and inheritance in Java object copying.
tag: Java
draft: false
---

- Cloneable is a flawed marker interface with no methods
- Object.clone() performs shallow field-by-field copy by default
- Shallow copies are dangerous for classes with mutable internal state
- Leads to shared mutable objects and subtle bugs
- Inheritance is fragile — subclasses must cooperate properly
- Final fields cannot be properly set by super.clone() if mutable
- Thread-safety must be manually ensured in clone
- No enforcement — clients can clone even if unintended
- If implementing: always call super.clone() first
- Manually deep-copy all mutable fields (arrays, collections)
- Handle CloneNotSupportedException (throw AssertionError)
- Prefer copy constructors: public MyClass(MyClass other)
- Or copy factories: public static MyClass copyOf(MyClass other)
- Copy constructors/factories are clearer, safer, more flexible
- They work with interfaces and avoid weird exceptions
- Modern Java: favor immutable objects or records (no cloning needed)
- JDK utilities like List.copyOf(), Set.copyOf() reinforce alternatives