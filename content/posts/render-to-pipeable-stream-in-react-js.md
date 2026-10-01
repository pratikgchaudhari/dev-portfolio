---
title: renderToPipeableStream() in React.js
date: 2025-10-01
summary: Stream server-rendered React HTML in Node.js with renderToPipeableStream and Suspense.
tag: React
draft: false
---

- `renderToPipeableStream` (React 18, `react-dom/server`) renders React components to a Node.js stream for server-side rendering with streaming HTML.
- Streams HTML incrementally, reducing TTFB, and supports Suspense for async rendering and progressive hydration.
- Usage: `import { renderToPipeableStream } from 'react-dom/server'; const stream = renderToPipeableStream(<App />, options);`.
- Options: `bootstrapScripts`, `bootstrapModules`, `onShellReady`, `onShellError`, `onAllReady`, `onError`.
- Streams initial HTML shell first, then additional content as resolved; client hydrates progressively.
- Error handling via `onShellError` (critical errors) and `onError` (non-critical).
- Methods: `pipe(writable)` sends data to a writable stream; `abort()` cancels streaming.
- Ideal for SSR apps (e.g., with Next.js) handling dynamic/async data.
- Requires Node.js, React 18+, and client-side hydration setup.
- Experimental API, may evolve; limited to Node.js without extra setup.