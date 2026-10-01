---
title: Vue.js setup() Hook
date: 2025-09-01
summary: Organize reactive state, computed values, methods, and lifecycle hooks with Vue's setup function.
tag: Vue.js
draft: false
---

- **Entry point for Composition API**: Central hub in Vue 3 for defining component logic, replacing or complementing Options API.
- **Creates reactive state**: Uses `ref` or `reactive` to define data that triggers UI updates on changes.
- **Sets up computed properties**: Defines memoized, reactive values with `computed` for efficient dependency updates.
- **Defines methods**: Declares functions for component logic or event handling within `setup()`.
- **Handles lifecycle hooks**: Supports hooks like `onMounted` or `onUnmounted` for lifecycle-specific code.
- **Runs before component creation**: Executes before `beforeCreate` and `created` hooks to initialize logic.
- **Promotes modular code**: Enables reusable, organized logic via composables for better maintainability.