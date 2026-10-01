---
title: flushSync() in React.js
date: 2025-09-28
summary: How flushSync forces immediate DOM updates, and the trade-offs of synchronous rendering.
tag: React
draft: false
---

`flushSync()` in React.js is a method from the `react-dom` package that forces React to synchronously update the DOM. Here's a breakdown in bullet points:

- **Purpose**: Ensures that state updates and DOM changes happen immediately, bypassing React's default asynchronous batching.
- **Import**: Available from `react-dom` as `import { flushSync } from 'react-dom';`.
- **Usage**: Wraps state updates or other React operations to make them synchronous, e.g., `flushSync(() => setState(newValue))`.
- **Key Use Cases**:
    - Immediate DOM updates for animations or transitions.
    - Ensuring state updates are reflected before performing DOM measurements (e.g., `getBoundingClientRect()`).
    - Handling urgent updates in response to user interactions, like form submissions.
- **Performance Impact**: Disables batching, which can lead to more frequent renders and potential performance issues if overused.
- **Context**: Typically used in event handlers or scenarios requiring immediate DOM access after a state change.
- **Example**:
  ```javascript
  import { flushSync } from 'react-dom';
  function handleClick() {
    flushSync(() => {
      setCount(count + 1);
    });
    console.log(document.querySelector('.count').textContent); // Reflects updated count
  }
  ```
- **Caution**: Use sparingly, as it disrupts React's optimized rendering pipeline.
- **Availability**: Supported in React 18 and later, aligning with concurrent rendering features.