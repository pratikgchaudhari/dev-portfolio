---
title: preloadModule() in React.js
date: 2025-09-26
summary: Notes on preloading JavaScript modules and preparing code before a React application needs it.
tag: React
draft: false
---

- `preloadModule()` is a React API to prefetch JavaScript modules for dynamic imports.
- It improves performance by loading modules in the background before they are needed.
- Syntax: `React.preloadModule(modulePath, options)`.
- `modulePath` is the path to the module to preload.
- `options` (optional) includes settings like `nonce` for security.
- Works with dynamic `import()` for lazy-loaded components.
- Does not execute the module; only fetches it.
- Useful for optimizing initial page load times.
- Supported in React 18 and later versions.
- Typically used in server-side rendering (SSR) or static site generation (SSG).