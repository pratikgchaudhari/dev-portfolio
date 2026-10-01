---
title: @Controller vs @RestController in Spring Boot
date: 2025-08-30
summary: Compare Spring controllers that render views with REST controllers that return response data.
tag: Spring
draft: false
---

- Purpose:
  - @Controller: Marks a class as a web controller for handling HTTP requests, typically returning view names for rendering. 
  - @RestController: Marks a class as a web controller for RESTful APIs, returning data directly as JSON or XML.
- Response Handling:
  - @Controller: Returns view names (e.g., for Thymeleaf or JSP) to render HTML pages or templates.
  - @RestController: Automatically serializes return values into JSON/XML, suitable for REST APIs.  
- Annotation Composition:
  - @Controller: A standalone annotation for traditional MVC web applications.
  - @RestController: Combines @Controller and @ResponseBody, applying @ResponseBody to all methods by default.  
- Use Case:
  - @Controller: Used for web applications where the response is a rendered webpage (e.g., server-side HTML). 
  - @RestController: Used for building RESTful web services where responses are data (e.g., JSON for APIs).  
- Method-Level Configuration:
  - @Controller: Requires @ResponseBody on methods to return data as JSON/XML instead of a view.
  - @RestController: No need for @ResponseBody as it’s implicitly applied to all methods.  
- Typical Output:
  - @Controller: Often returns a String (view name) or ModelAndView for rendering UI.
  - @RestController: Returns objects (e.g., POJOs, collections) serialized to JSON/XML.
