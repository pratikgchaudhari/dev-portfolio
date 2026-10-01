---
title: cacheSignal() in React.js
date: 2025-10-05
summary: Use cacheSignal to tie asynchronous work to the lifetime of a React server render.
tag: React
draft: false
---

- **Introduction**: cacheSignal is a React API introduced in React 19.2, primarily used with React Server Components (RSC) to manage the lifetime of cached operations.
- **Purpose**: It provides an AbortSignal that aborts when React finishes rendering, allowing cancellation of unnecessary in-flight work like fetches or database queries to optimize performance and avoid wasted resources.
- **When to Use**: Employ cacheSignal in RSC for aborting requests or ignoring cancellation-related errors after rendering completes; it's not suitable for async work initiated outside of rendering.
- **Usage Syntax**: Import from 'react' and call cacheSignal() to get the signal, often passed to APIs like fetch: `await fetch(url, { signal: cacheSignal() });`.
- **Parameters**: Accepts no parameters.
- **Return Value**: Returns an AbortSignal during rendering; otherwise, returns null (e.g., in Client Components or outside rendering).
- **Example - Canceling Requests**: Combine with cache() for deduped, abortable fetches: 
```javascript
const dedupedFetch = cache(fetch); 
await dedupedFetch(url, { signal: cacheSignal() });
```
- **Example - Ignoring Errors**: Check `cacheSignal()?.aborted` to ignore errors from aborted operations: 
```javascript
try { 
    return await queryDatabase(id); 
} catch (x) { 
    if (!cacheSignal()?.aborted) 
        logError(x); 
    return null; 
}
```
- **Pitfalls**: Will not abort work started outside rendering, like pre-initiated fetches; always call during rendering for effectiveness.
- **Caveats**: Currently limited to RSC (returns null in Client Components); future updates may extend to client-side cache refreshes. Related to cache() for deduping.