---
title: Vue.js Reactivity System
date: 2025-08-31
summary: How Vue tracks reactive data and updates the UI when application state changes.
tag: Vue.js
draft: false
---

- **Reactivity System**: Vue.js uses a reactive data system to automatically update the UI when data changes.
- **Reactive Objects**: Data properties in a Vue instance or component are made reactive using `reactive()` or `ref()` in Vue 3.
- **Getters/Setters**: Vue wraps data properties with getters and setters to track changes and dependencies.
- **Dependency Tracking**: When a component renders, Vue tracks which reactive properties are accessed.
- **Change Detection**: Modifying a reactive property triggers its setter, notifying dependent components.
- **Automatic Updates**: Vue re-renders affected components efficiently, updating only the necessary DOM parts.
- **Reactivity Caveats**: Only properties present during initialization are reactive; new properties need `set()` or reactive APIs.
- **Vue 3 Proxy**: Uses ES6 Proxies for better performance and reactivity compared to Vue 2’s Object.defineProperty.
- **Refs for Primitives**: `ref()` creates reactive single-value wrappers for primitives like strings or numbers.
- **DOM Sync**: Reactive changes propagate to the DOM via Vue’s virtual DOM, ensuring minimal updates.