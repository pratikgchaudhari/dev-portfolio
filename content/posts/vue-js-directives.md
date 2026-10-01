---
title: Vue.js Directives
date: 2025-09-02
summary: A reference to Vue directives for binding, forms, conditional rendering, and lists.
tag: Vue.js
draft: false
---

- **v-bind**: Binds an attribute to an expression, dynamically updating the attribute’s value (e.g., `:src="imageUrl"`).
- **v-model**: Creates a two-way binding on form inputs or components, syncing data with user input.
- **v-if**: Conditionally renders an element or template based on a truthy expression (e.g., `v-if="isVisible"`).
- **v-else**: Used with `v-if` to render an element when the `v-if` condition is false (no expression needed).
- **v-else-if**: Used with `v-if` to specify an additional conditional rendering case (e.g., `v-else-if="condition"`).
- **v-show**: Toggles an element’s visibility using CSS `display` based on a truthy expression (e.g., `v-show="isVisible"`).
- **v-for**: Renders a list by iterating over an array or object (e.g., `v-for="item in items"`).
- **v-on**: Attaches event listeners to elements, triggering methods or expressions (e.g., `v-on:click="handleClick"` or `@click="handleClick"`).
- **v-slot**: Specifies the content for a named slot in a component (e.g., `v-slot:header` or `#header`).
- **v-pre**: Skips compilation for the element and its children, rendering raw mustache tags as plain text.
- **v-cloak**: Keeps an element hidden until Vue compilation is complete (used with CSS `[v-cloak] { display: none }`).
- **v-once**: Renders the element or component only once, ignoring subsequent updates.
- **v-memo**: Caches a template’s rendering based on dependencies, improving performance for static content (Vue 3).
- **v-html**: Updates an element’s inner HTML with the provided expression (use cautiously due to XSS risks).
- **v-text**: Updates an element’s text content with the provided expression (e.g., `v-text="message"`).