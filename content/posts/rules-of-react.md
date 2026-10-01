---
title: Rules of React
date: 2025-09-13
summary: A checklist of React practices for components, hooks, state updates, and rendering.
tag: React
draft: false
---

1. Components return a single JSX element (or null/undefined), using a parent like `<div>` or `<>` for multiple elements.
2. Components must be pure, avoiding side effects during rendering (use `useEffect` for side effects).
3. Update state immutably using setter functions, not direct mutations.
4. Call hooks (`useState`, `useEffect`, etc.) only at the top level of components or custom hooks, not in loops/conditionals.
5. Use hooks only in functional components or custom hooks, not regular functions or classes.
6. Treat props as read-only; never modify them directly.
7. Ensure JSX tags are properly closed (self-closing or with a closing tag).
8. Use unique, stable `key` props for list items, avoiding array indices unless static.
9. Optimize to prevent unnecessary re-renders with `React.memo`, `useMemo`, or `useCallback`.
10. Use descriptive prop names, avoiding reserved words (e.g., use `className` instead of `class`, `htmlFor` instead of `for`).
11. Name components with PascalCase and props/variables with camelCase.
12. Pass event handlers as functions, not strings (e.g., `onClick={handleClick}`).
13. Handle side effects in `useEffect` with proper cleanup functions.
14. Avoid adding props to Fragments except for `key` in lists.
15. Define event handlers outside JSX to avoid creating new functions on each render.