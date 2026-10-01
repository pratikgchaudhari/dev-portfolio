---
title: Provide & Inject Vue.js
date: 2025-09-10
summary: Share dependencies across a Vue component tree using provide and inject.
tag: Vue.js
draft: false
---

- **Provide**: Allows a parent component to share data with all its descendants, regardless of nesting level.
- **Inject**: Enables a descendant component to access data provided by an ancestor component.
- **Provide Usage**: Defined in a parent component using the `provide` option or `provide()` function in Composition API.
- **Inject Usage**: Accessed in a child component using the `inject` option or `inject()` function in Composition API.
- **Reactivity**: Provided data can be reactive if using reactive objects or refs in Composition API.
- **Scope**: Data is available only to components in the descendant tree of the providing component.
- **Dynamic Updates**: Changes to provided data propagate to injected components if reactive.
- **Default Values**: Inject can specify default values if the provided key is not found.
- **Use Case**: Ideal for sharing global state or dependencies like themes, configs, or services without prop drilling.