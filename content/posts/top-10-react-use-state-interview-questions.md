---
title: Top 10 React.js useState() Interview Questions
date: 2025-09-13
summary: Interview questions and examples covering state initialization, updates, and React rendering.
tag: React
draft: false
---

1. **What is `useState()`?**  
   A React hook to manage state in functional components with a state variable and update function.

2. **How to use `useState()`?**
   ```jsx
   import { useState } from 'react';
   const [count, setCount] = useState(0);
   <button onClick={() => setCount(count + 1)}>{count}</button>;
   ```

3. **What does `useState()` return?**  
   An array with the current state and its update function.

4. **Can `useState()` hold objects/arrays?**  
   Yes, it supports any data type (objects, arrays, strings, etc.).

5. **How to update an object in `useState()`?**
   ```jsx
   const [user, setUser] = useState({ name: '', age: 0 });
   setUser({ ...user, name: 'John' });
   ```

6. **Why isn’t state updated immediately?**  
   `setState` is asynchronous; React batches updates for performance.

7. **How to use previous state in `setState`?**
   ```jsx
   setCount(prevCount => prevCount + 1);
   ```

8. **Can `useState` initialize with a function?**  
   Yes, for lazy initialization:
   ```jsx
   const [value, setValue] = useState(() => expensiveComputation());
   ```

9. **How to handle multiple state variables?**  
   Use multiple `useState` calls:
   ```jsx
   const [name, setName] = useState('');
   const [age, setAge] = useState(0);
   ```

10. **Why does state update cause an infinite loop?**  
    `setState` in `useEffect` or render without conditions/dependencies causes re-renders; use dependency arrays or conditionals.