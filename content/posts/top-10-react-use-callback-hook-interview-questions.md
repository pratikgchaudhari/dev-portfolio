---
title: Top 10 React.js useCallback() Interview Question
date: 2025-09-14
summary: Interview questions on callback memoization, dependency arrays, and React rendering performance.
tag: React
draft: false
---

1. **What is the `useCallback` hook in React?**  
   > It memoizes a callback function, preventing unnecessary re-creations on re-renders unless dependencies change.

2. **What is the syntax of `useCallback`?**  
   > `const memoizedCallback = useCallback(callback, [dependencies]);`

3. **Why would you use `useCallback`?**  
   > To optimize performance by memoizing functions passed as props to prevent unnecessary re-renders in child components.

4. **How does `useCallback` differ from `useMemo`?**  
   > `useCallback` memoizes functions, while `useMemo` memoizes computed values.

5. **When should you avoid using `useCallback`?**  
   > Avoid it for simple functions or when memoization doesn't provide significant performance benefits.

6. **What happens if you omit the dependency array in `useCallback`?**  
   > The callback is re-created on every render, negating the memoization benefit.

7. **Can `useCallback` improve performance in all cases?**  
   > No, it only helps when preventing unnecessary re-renders in components that rely on stable function references.

8. **How does `useCallback` work with React's dependency array?**  
   > It re-creates the memoized function only when one or more dependencies change.

9. **Can `useCallback` be used with inline functions?**  
   > Yes, but it’s typically used to memoize functions defined inline to maintain reference stability.

10. **What is a common use case for `useCallback`?**  
    > Passing a stable callback to a child component or a hook like `useEffect` to avoid unnecessary re-renders or effects.