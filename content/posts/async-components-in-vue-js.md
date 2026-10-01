---
title: Async Components in Vue.js
date: 2025-09-07
summary: Load Vue components on demand with dynamic imports, loading states, and error handling.
tag: Vue.js
draft: false
---

- **Definition**: Async components in Vue.js allow loading components on-demand, improving performance.
- **Syntax**: Define using `defineAsyncComponent(() => import('Component.vue'))`.
- **Lazy Loading**: Loads component only when needed, reducing initial bundle size.
- **Dynamic Import**: Uses JavaScript’s dynamic `import()` for asynchronous loading.
- **Loading State**: Supports `loadingComponent` to display a placeholder while loading.
- **Error Handling**: Uses `errorComponent` to show fallback UI if loading fails.
- **Timeout**: Configurable `timeout` to handle slow network conditions.
- **Suspense Integration**: Works with `<Suspense>` for better async handling in Vue 3.
- **Use Case**: Ideal for large apps to load non-critical components on demand.