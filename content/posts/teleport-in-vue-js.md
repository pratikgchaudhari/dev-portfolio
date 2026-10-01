---
title: <Teleport> in Vue.js
date: 2025-09-11
summary: Render modals and other Vue content outside their parent DOM hierarchy with Teleport.
tag: Vue.js
draft: false
---

- **Purpose**: `<Teleport>` in Vue.js allows rendering a component's content to a different DOM location outside its parent hierarchy.
- **Syntax**: `<Teleport to="selector">...</Teleport>`, where `to` specifies the target DOM element (e.g., `#id`, `.class`, or `body`).
- **Use Case**: Ideal for modals, popups, or tooltips that need to be appended to a specific DOM location, like `<body>`.
- **Scope Retention**: The teleported content retains its Vue component scope, reactivity, and context, behaving as if it’s still in its original place.
- **Multiple Teleports**: Multiple `<Teleport>` components can target the same DOM element; they append in order.
- **Dynamic Updates**: The `to` prop can be dynamic (e.g., `:to="target"`), allowing runtime changes to the target.
- **Disabled Option**: Use `disabled` prop to prevent teleporting, rendering content in its original place (e.g., `<Teleport :disabled="true">`).
- **SSR Support**: Works with Server-Side Rendering, ensuring content is correctly placed during hydration.
- **Caveats**: Target DOM element must exist when `<Teleport>` mounts; otherwise, it throws an error.