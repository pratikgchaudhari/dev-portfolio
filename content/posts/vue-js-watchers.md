---
title: Watchers in Vue.js
date: 2025-09-06
summary: React to data changes with Vue watchers, including immediate and deep watching options.
tag: Vue.js
draft: false
---

- **Reactive Data Monitoring**: Tracks changes to reactive properties (data, props, computed) and runs a callback.
- **Immediate Execution**: Runs the watcher callback immediately with `immediate: true`.
- **Deep Watching**: Monitors nested objects/arrays for changes with `deep: true`.
- **Custom Callback Function**: Executes a custom function with new/old values and `onCleanup` (Vue 3).
- **Watching Multiple Sources**: Observes multiple reactive sources (Vue 3) using an array.
- **Flush Timing Control**: Sets callback timing (`pre`, `post`, `sync`) relative to DOM updates.
- **Watcher Lifecycle**: Auto-cleans up when the component unmounts.
- **Dynamic Watcher Creation**: Programmatically creates watchers in Vue 3’s `setup` or script setup.
- **Computed Property Watching**: Observes computed properties for derived value changes.
- **Error Handling**: Handles errors in the callback using try-catch.
- **Support for Reactive/Ref**: Watches `reactive` objects or `ref` values (Vue 3).
- **Once Watcher**: Executes only once on the first change with `once: true` (Vue 3).