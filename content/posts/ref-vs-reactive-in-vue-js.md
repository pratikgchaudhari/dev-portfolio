---
title: Vue.js ref() vs reactive()
date: 2025-08-31
summary: Compare ref and reactive for managing values and objects in Vue's reactivity system.
tag: Vue.js
draft: false
---

| **Aspect**                | **`ref()`**                                                                 | **`reactive()`**                                                             |
|---------------------------|-----------------------------------------------------------------------------|------------------------------------------------------------------------------|
| **Definition**            | Creates a reactive reference to a single value (primitive or object).       | Creates a reactive object/array with deeply reactive properties.             |
| **Input Type**            | Accepts primitives (e.g., string, number) and objects/arrays.               | Only accepts objects or arrays (not primitives).                            |
| **Accessing Value**       | Accessed via `.value` in JavaScript; unwrapped in templates.                | Direct property access (e.g., `obj.key`); no `.value` needed.               |
| **Reactivity Behavior**   | Only `.value` is reactive; reassigning `.value` preserves reactivity.       | Entire object and nested properties are reactive; reassigning object breaks reactivity. |
| **Immutability**          | `ref` variable is immutable; `.value` can be changed.                       | Reactive object is immutable; properties can be modified.                   |
| **Use Case**              | Best for single values or standalone state (e.g., counter, string).         | Best for complex objects with multiple properties (e.g., form data).        |
| **Performance**           | Lightweight for single values.                                              | Heavier for deeply nested objects due to recursive reactivity.              |
| **Template Usage**        | Used directly (e.g., `{{ count }}`); `.value` unwrapped automatically.      | Properties accessed directly (e.g., `{{ state.count }}`).                  |
| **Reassignment**          | Changing `.value` (e.g., `count.value = 5`) preserves reactivity.           | Reassigning object (e.g., `state = newObj`) breaks reactivity unless re-wrapped. |
| **Example**               | ```const count = ref(0); count.value++```                                  | ```const state = reactive({ count: 0 }); state.count++```                   |