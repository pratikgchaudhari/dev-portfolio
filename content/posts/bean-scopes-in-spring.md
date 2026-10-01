---
title: Bean Scopes in Spring
date: 2025-08-31
summary: A reference to Spring bean scopes and the lifetimes of the objects they manage.
tag: Spring
draft: false
---

1. **Singleton** (Default)
    - Only one instance of the bean is created per Spring IoC container.
    - Shared across all requests for that bean.
    - Suitable for stateless beans, like services or DAOs.

2. **Prototype**
    - A new instance is created each time the bean is requested.
    - Useful for stateful beans where each usage needs a fresh instance.
    - Lifecycle management (e.g., destruction) is not handled by Spring.

3. **Request**
    - A new bean instance is created for each HTTP request.
    - Only valid in a web-aware Spring ApplicationContext.
    - Ideal for web applications where data is specific to a single request.

4. **Session**
    - A new bean instance is created for each HTTP session.
    - Only valid in a web-aware Spring ApplicationContext.
    - Suitable for user-specific data that persists across multiple requests in a session.

5. **Application**
    - A single bean instance is created per ServletContext.
    - Only valid in a web-aware Spring ApplicationContext.
    - Used for application-wide data shared across all users and sessions.

6. **WebSocket**
    - A new bean instance is created for each WebSocket connection.
    - Only valid in a web-aware Spring ApplicationContext with WebSocket support.
    - Useful for managing state specific to a WebSocket session.