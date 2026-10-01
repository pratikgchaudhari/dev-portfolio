---
title: useEffectEvent() in React.js
date: 2025-10-05
summary: Separate non-reactive logic from React Effects with useEffectEvent.
tag: React
draft: false
---

- **Purpose**: `useEffectEvent` is a React Hook that allows you to extract non-reactive logic from your Effects into a reusable function known as an Effect Event. This helps in reading the latest props and state inside an Effect without causing the Effect to re-run when those values change.
- **When to Use It**: Employ `useEffectEvent` for logic that doesn't depend on changing values, such as accessing non-reactive state (e.g., `numberOfItems`) in its latest form without triggering Effect re-runs, while keeping reactive values (e.g., `url`) as dependencies or arguments.
- **API Details**:
    - **Parameters**: Accepts a `callback` function that contains the logic for the Effect Event. This callback always accesses the latest values from props and state when invoked, avoiding stale closure issues.
    - **Return Value**: Returns an Effect Event function that can be called within `useEffect`, `useLayoutEffect`, or `useInsertionEffect`.
- **Usage Examples**:
    - **Basic Example**: Define an Effect Event like `const onConnected = useEffectEvent(() => { showNotification('Connected!', theme); });` and call it inside a `useEffect` that depends on reactive values like `roomId`, allowing `theme` to be read freshly without being a dependency.
    - **Reading Latest Props and State**: Use it in scenarios like logging visits with `const onNavigate = useEffectEvent((visitedUrl) => { logVisit(visitedUrl, numberOfItems); });`, where the Effect re-runs on `url` changes but `numberOfItems` is always current without causing re-runs.
- **Caveats**:
    - Only invoke Effect Events inside Effects; define them just before the Effect that uses them, and do not pass them to other components or hooks. The `eslint-plugin-react-hooks` (version 6.1.1 or higher) enforces proper usage.
    - It's not a way to skip specifying dependencies in an Effect's array, as that can obscure bugs—prefer explicit dependencies or refs for value comparisons.
    - Reserve it for non-reactive logic independent of changing values.