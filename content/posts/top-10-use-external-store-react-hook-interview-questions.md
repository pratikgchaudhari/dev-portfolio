---
title: Top 10 useExternalStore() React.js hook Interview Questions
date: 2025-09-16
summary: Interview questions on subscribing to external state with React's useSyncExternalStore hook.
tag: React
draft: false
---

1. What is the `useSyncExternalStore` hook in React?  
   A hook to subscribe to an external store and sync its state with React components.

2. When was `useSyncExternalStore` introduced in React?  
   Introduced in React 18 to support concurrent rendering and external store integration.

3. What problem does `useSyncExternalStore` solve?  
   Prevents tearing in concurrent rendering by safely syncing external store state.

4. What are the parameters of `useSyncExternalStore`?  
   Takes `subscribe`, `getSnapshot`, and optional `getServerSnapshot` functions.

5. What does the `subscribe` function do in `useSyncExternalStore`?  
   Subscribes to the external store and returns an unsubscribe function.

6. What is the role of `getSnapshot` in `useSyncExternalStore`?  
   Returns the current value of the external store’s state.

7. When is `getServerSnapshot` used?  
   Provides the initial state during server-side rendering or hydration.

8. How does `useSyncExternalStore` prevent state tearing?  
   Ensures consistent state reads during concurrent rendering by validating snapshots.

9. Can `useSyncExternalStore` replace Redux or other state management libraries?  
   Not a replacement, but enables libraries like Redux to integrate with React’s concurrent features.

10. What happens if the store state changes frequently?  
    React re-renders the component only when the snapshot value changes.

11. How does `useSyncExternalStore` handle cleanup?  
    Automatically calls the unsubscribe function returned by `subscribe` on unmount.

12. Is `useSyncExternalStore` used directly in applications?  
    Typically used by state management libraries, not directly in app code.

13. What’s the difference between `useSyncExternalStore` and `useState`?  
    `useSyncExternalStore` syncs with external stores, while `useState` manages internal component state.

14. Can `useSyncExternalStore` be used with browser APIs?  
    Yes, it can subscribe to browser APIs like `window.localStorage` or `navigator`.

15. What is an example use case for `useSyncExternalStore`?  
    Syncing a component with a browser’s `localStorage` or a third-party store like Zustand.