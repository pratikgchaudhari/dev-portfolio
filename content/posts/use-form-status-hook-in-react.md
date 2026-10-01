---
title: useFormStatus() hook in React
date: 2026-01-10
summary: Read a parent form's submission status to display pending states and feedback in React.
tag: React
draft: false
---

- **Purpose**: `useFormStatus` is a React hook that provides status information about the last submission of its parent `<form>`, useful for managing UI during form actions (e.g., with server actions).

- **Return Value**: An object with these properties:
    - `pending`: Boolean — `true` if the parent form is currently submitting, `false` otherwise.
    - `data`: `FormData` object (or `null`) containing the data being submitted.
    - `method`: String — `'get'` or `'post'`, indicating the form's HTTP method.
    - `action`: Function reference (or `null`) to the form's `action` prop.

- **Usage Requirements**:
    - Must be called inside a component that is a child of a `<form>`.
    - Only reads status from the nearest parent `<form>`.
    - Works in Client Components; commonly used with React Server Actions.

- **Common Use Cases**:
    - Disable submit button during submission: `disabled={status.pending}`.
    - Show loading indicators or spinners when `pending` is `true`.
    - Display submitted data temporarily via `data`.

- **Caveats**:
    - Returns default values (e.g., `pending: false`) if no parent `<form>` exists.
    - Does not track forms rendered in the same component or unrelated forms.
    - Experimental/stable depending on React version; check current docs for support.