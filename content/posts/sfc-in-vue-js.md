---
title: Single-file Components in Vue.js
date: 2025-09-03
summary: How Vue single-file components combine templates, scripts, and styles in one file.
tag: Vue.js
draft: false
---

- SFC stands for Single File Component in Vue.js
- It’s a file format that encapsulates a Vue component’s template, script, and styles in a single `.vue` file
- Each SFC typically has three main sections: `<template>`, `<script>`, and `<style>`
- The `<template>` section contains the HTML structure of the component
- The `<script>` section includes JavaScript or TypeScript logic, defining the component’s behavior
- The `<style>` section holds CSS for styling the component
- SFCs improve code organization by keeping related code in one file
- They support scoped CSS using the `scoped` attribute in the `<style>` tag
- SFCs are processed by build tools like Vite or Webpack with Vue Loader
- They enable component-based development for reusable and modular code
- SFCs can include additional features like `<script setup>` for concise syntax
- They support preprocessing languages like SCSS or TypeScript with proper configuration
- SFCs make it easier to maintain and scale Vue.js applications