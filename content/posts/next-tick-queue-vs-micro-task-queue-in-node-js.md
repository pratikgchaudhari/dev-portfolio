---
title: Next Task Queue vs Micro Task Queue in Node.js
date: 2026-01-11
summary: Compare process.nextTick with Promise microtasks and their execution order in Node.js.
tag: JavaScript
draft: false
---

| #  | Aspect                              | nextTickQueue (`process.nextTick()`)                                                        | Microtask Queue (Promises / queueMicrotask)                                        | Winner (Higher Priority)  |
|----|-------------------------------------|---------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------|---------------------------|
| 1  | Execution priority                  | Highest priority among all async mechanisms                                                 | Very high, but **after** nextTick                                                  | nextTick                  |
| 2  | Execution timing                    | After current operation finishes, **before** next event loop phase & before microtasks      | After current operation + after all nextTick callbacks                             | nextTick                  |
| 3  | Part of official event loop phases? | **No** – technically outside (runs between phases / after current C++ → JS transition)      | **Yes** – standard V8 microtask queue (used by browser & Node)                     | —                         |
| 4  | Managed by                          | Node.js (libuv layer)                                                                       | V8 engine                                                                          | —                         |
| 5  | Typical callbacks                   | `process.nextTick(fn)`                                                                      | `Promise.then()`, `catch()`, `finally()`, `queueMicrotask()`, `await` continuation | —                         |
| 6  | Order when both queued at same time | Always executed **before** any microtask in the same tick                                   | Executed **after** all nextTick callbacks are drained                              | nextTick                  |
| 7  | Starvation risk                     | **Very high** – recursive nextTick can completely starve the event loop (I/O, timers, etc.) | High, but less dangerous than nextTick (still can delay phases)                    | nextTick (more dangerous) |
| 8  | Recommended usage (modern code)     | Use **sparingly** – mainly for error handling, cleanup, or library internals                | **Preferred** for most user-land async continuation logic                          | Microtask Queue           |
| 9  | Error handling behavior             | Errors are thrown synchronously in next tick (can crash process if uncaught)                | Errors are **rejected** promises → better for `try/catch` & `.catch()`             | Microtask (safer)         |
| 10 | CommonJS vs ESM subtle difference   | Always first in both                                                                        | In ESM: sometimes behaves slightly earlier due to module loading being microtask   | —                         |
| 11 | Official docs recommendation        | "Use with care" – can block event loop                                                      | Standard way for async continuation (preferred in new code)                        | Microtask Queue           |