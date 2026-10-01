---
title: <KeepAlive> in Vue.js
date: 2025-09-05
summary: Preserve dynamic component state and manage cached instances with Vue's KeepAlive component.
tag: Vue.js
draft: false
---

- **Caches Component Instances:** Preserves the state of dynamic components by caching their instances in memory, preventing destruction when switched out.
- **Prevents Re-rendering:** Avoids re-rendering cached components, reducing performance overhead for complex or frequently toggled components.
- **State Persistence:** Maintains component state (e.g., form inputs, scroll positions) across switches, ensuring a seamless user experience.
- **Dynamic Component Support:** Works with the `<component :is="...">` element to cache dynamically switched components.
- **Include/Exclude Props:** Customizes caching behavior with `include` and `exclude` props, accepting comma-delimited strings, RegExp, or arrays to specify which components to cache or exclude based on their `name` option.
- **Max Cache Limit:** Limits the number of cached instances using the `max` prop, implementing a Least Recently Used (LRU) cache strategy to manage memory by removing the least recently accessed instance when the limit is exceeded.
- **Lifecycle Hooks:** Supports `onActivated()` and `onDeactivated()` (or `activated` and `deactivated` in Options API) to run code when a component is activated or deactivated, replacing `mounted`/`unmounted` for cached components.
- **Abstract Component:** Does not render a DOM element or appear as a component in the DOM, functioning as a virtual node (vnode) for caching.
- **Router Integration:** Can wrap `<router-view>` to cache route components, preserving state when navigating between routes.
- **Conditional Caching:** Requires components to have a `name` option for conditional caching with `include`/`exclude` props.
- **Memory Trade-off:** Trades memory usage for performance by keeping components in memory, requiring careful use to avoid excessive memory consumption.
- **No Manual Cache Deletion:** Lacks a built-in method to manually clear specific cached instances, relying on LRU strategy or component destruction.