---
title: Why one should use Varargs judiciously in Java?
date: 2026-01-17
summary: Balance the convenience of varargs with array allocation costs and generic type-safety concerns.
tag: Java
draft: false
---

- Varargs simplify APIs by eliminating explicit array creation from callers
- Great for methods like String.format(), Arrays.asList(), List.of()
- Every varargs call allocates a new array (even for zero args)
- Performance cost significant in tight loops or performance-critical code
- Generic varargs risky — can cause heap pollution and ClassCastException
- Arrays are covariant; generic arrays unsafe → unchecked warnings
- Avoid generic varargs when possible; prefer fixed-parameter overloads (0–3 args)
- If generic varargs needed, ensure method is truly safe and annotate @SafeVarargs
- Never store the varargs array or pass it to untrusted code
- Place varargs as the last parameter (only one varargs per method allowed)
- Watch for overloading ambiguity with fixed vs. varargs signatures
- Safe varargs methods copy arguments or wrap without exposing array
- Alternatives: require collections/arrays if varargs risks too high
- Modern immutable collections (List.of, Set.of) use varargs idiomatically and safely