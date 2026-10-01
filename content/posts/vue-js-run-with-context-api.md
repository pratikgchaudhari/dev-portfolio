---
title: app.runWithContext() in Vue.js
date: 2025-10-04
summary: Run a function within a Vue application context using app.runWithContext.
tag: Vue.js
draft: false
---

The `app.runWithContext()` API in Vue.js is a method introduced in Vue 3 for executing a function within the context of a specific Vue application instance. Here’s a concise explanation in bullet-point format:

- **Purpose**: Executes a function with the Vue application context, ensuring access to the app’s dependency injection, global properties, and other app-level configurations.
- **Availability**: Part of the Vue 3 application instance API, available since Vue 3.0.
- **Syntax**: `app.runWithContext(callback: () => T): T`
    - `app`: The Vue application instance (created via `createApp`).
    - `callback`: The function to execute within the app’s context.
    - Returns the result of the `callback` function.
- **Key Use Case**: Useful in scenarios where you need to access app-level context (e.g., `provide`, `globalProperties`, or `app.config`) outside of a component, such as in utility functions or server-side rendering (SSR) logic.
- **Context Preservation**: Ensures the callback runs with the correct `app` context, which is critical when multiple Vue apps coexist in the same JavaScript runtime.
- **Example**:
  ```javascript
  import { createApp } from 'vue';

  const app = createApp({});
  app.provide('key', 'value');

  const result = app.runWithContext(() => {
    // Access app context, e.g., injected values
    return inject('key'); // Returns 'value'
  });
  console.log(result); // Output: 'value'
  ```
- **When to Use**:
    - Accessing `provide`/`inject` values outside components.
    - Interacting with global properties or app config in non-component code.
    - Ensuring context in asynchronous operations or external libraries.
- **Limitations**:
    - Only works with the specific `app` instance it’s called on.
    - Not needed within components, as they inherently have access to the app context.
- **Relation to SSR**: Commonly used in server-side rendering to ensure proper context propagation when rendering components outside the standard component lifecycle.

This API is particularly valuable for advanced use cases involving app-level context management in complex Vue applications.