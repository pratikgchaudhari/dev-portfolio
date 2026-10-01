---
title: nextTick() in Vue.js
date: 2025-09-23
summary: Use nextTick to run code after Vue applies reactive changes to the DOM.
tag: Vue.js
draft: false
---

- `nextTick()` defers a callback until the next DOM update cycle in Vue.js.
- Ensures DOM updates from reactive data changes are applied before the callback runs.
- Used to access or manipulate the DOM after Vue updates it (e.g., post `v-for` change).
- Syntax: `Vue.nextTick(callback)` globally or `this.$nextTick(callback)` in components.
- Batches DOM updates for performance, executing callbacks in the next microtask.
- Supports `await this.$nextTick()` for async functions.
- Example: Update data, then use `this.$nextTick(() => console.log(this.$el.textContent))`.
- Runs in the microtask queue, after JavaScript execution and before browser repaint.