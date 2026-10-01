---
title: <Activity> in React.js
date: 2025-10-04
summary: Preserve component state while controlling visibility and background work with React's Activity component.
tag: React
draft: false
---

-  The `<Activity>` component, introduced as a stable feature in React 19.2, allows you to partition your app into distinct "activities" that can be controlled and prioritized separately. It provides a way to hide and restore UI elements while preserving their internal state, serving as an alternative to conditional rendering or CSS hiding, which can lead to state loss or performance issues.

- It supports two modes via the `mode` prop: `'visible'` (default, renders normally) and `'hidden'` (visually hides children using `display: none`, unmounts effects, and defers updates to low priority). This enables background rendering without impacting visible content.

- Unlike unmounting via conditional rendering, `<Activity>` saves the internal state (e.g., component state, form inputs, video playback position) when hidden, allowing instant restoration when switched back to visible. This is useful for tabs, sidebars, or navigation where users expect preserved progress.

- In hidden mode, Effects are destroyed and their cleanup functions executed to avoid unnecessary side effects like subscriptions or timers. Effects only run when visible. Components should use proper cleanup (e.g., via `useLayoutEffect` for media pausing) to handle persistent DOM behaviors.

- Hidden content renders at lower priority, enabling pre-rendering (e.g., loading data, CSS, images) without blocking the main thread. It supports Selective Hydration for faster page loads by hydrating visible parts first and participates in features like Partial Prerendering.

- When nested in a ViewTransition, visibility changes trigger enter/exit animations during transitions (e.g., via `startTransition`). It works with Suspense for pre-fetching data in hidden mode, but only for Suspense-enabled sources like Relay or Next.js; Effect-based fetching isn't supported.

- Basic usage: `<Activity mode={isVisible ? 'visible' : 'hidden'}><MyComponent /></Activity>`. For tabs: Wrap each tab panel to preserve state on switches. For pre-rendering: Hide slow-loading components initially to load in background. Use StrictMode to test cleanup reliability.

- Limitations include no support for Effect-based data fetching in hidden mode and potential side effects from persistent DOM (e.g., videos playing). An official API for Suspense-enabled data fetching is planned. Future modes may expand use cases for different priorities or state management.