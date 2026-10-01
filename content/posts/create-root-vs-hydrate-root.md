---
title: Create Root vs Hydrate Root in React.js
date: 2025-09-26
summary: Compare creating a client-rendered React root with hydrating HTML rendered on the server.
tag: React
draft: false
---

| **Feature**            | **createRoot**                                                                  | **hydrateRoot**                                                                   |
|------------------------|---------------------------------------------------------------------------------|-----------------------------------------------------------------------------------|
| **Purpose**            | Used to create a new React root for rendering a React application from scratch. | Used to hydrate a server-rendered React application on the client side.           |
| **Primary Use Case**   | Client-side rendering (CSR) for building a new React app in the browser.        | Server-side rendering (SSR) or static site generation (SSG) hydration.            |
| **DOM Interaction**    | Renders the React component tree into an empty DOM container.                   | Hydrates an existing server-rendered DOM by attaching React event handlers.       |
| **DOM Container**      | Requires an empty DOM element to render the React app into.                     | Requires a DOM element with pre-rendered server-side HTML content.                |
| **Typical Usage**      | Used when the app is fully rendered on the client side.                         | Used when the initial HTML is rendered on the server to improve performance.      |
| **Performance Impact** | Full rendering happens in the browser, which may be slower for initial load.    | Faster initial load as it reuses server-rendered HTML, only adding interactivity. |
| **API Signature**      | `createRoot(container[, options])`                                              | `hydrateRoot(container, element[, options])`                                      |
| **Error Handling**     | Does not expect pre-existing DOM content; overwrites the container.             | Expects matching server-rendered DOM; mismatches may cause errors or warnings.    |
| **When to Use**        | Use for new React apps or when server-side rendering is not involved.           | Use for hydrating server-rendered apps to make them interactive on the client.    |