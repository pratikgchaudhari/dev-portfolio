---
title: Vue.js Lifecycle Hooks
date: 2025-08-31
summary: A reference to the hooks that run as Vue components are created, mounted, updated, and removed.
tag: Vue.js
draft: false
---

- **beforeCreate**: Called immediately after instance initialization, before data observation and event/watcher setup.
- **created**: Called after the instance is created, with data observation, computed properties, methods, and watchers set up, but before DOM mounting.
- **beforeMount**: Called before the component is mounted to the DOM, after the template is compiled.
- **mounted**: Called after the component is mounted to the DOM, with the element fully rendered and accessible.
- **beforeUpdate**: Called when reactive data changes, before the DOM is re-rendered and patched.
- **updated**: Called after the DOM has been updated due to reactive data changes.
- **beforeUnmount**: Called before a component is unmounted, allowing cleanup tasks while the component is still fully functional.
- **unmounted**: Called after the component is unmounted and destroyed, with all directives, listeners, and resources cleaned up.
- **errorCaptured**: Called when an error from a descendant component is captured, allowing error handling (receives error, instance, and info).
- **renderTracked**: Called when a reactive dependency is tracked during rendering, useful for debugging (receives debugger event).
- **renderTriggered**: Called when a render is triggered by a reactive dependency change, useful for debugging (receives debugger event).
- **activated**: Called when a component inside `<KeepAlive>` is activated (re-displayed from cache).
- **deactivated**: Called when a component inside `<KeepAlive>` is deactivated (cached but not destroyed).
- **serverPrefetch** (SSR only): Called on the server before rendering to prefetch data for server-side rendering.