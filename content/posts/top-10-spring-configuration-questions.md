---
title: Top Spring Configuration Questions
date: 2025-10-06
summary: Questions and answers on XML, annotations, Java configuration, and Spring bean definitions.
tag: Spring
draft: false
---

1. What is a Spring configuration file?  
   A Spring configuration file is typically an XML file (e.g., applicationContext.xml) that defines beans, their dependencies, and other application settings for the Spring IoC container to manage.

2. What are the different ways to configure Spring?  
   Spring can be configured using XML-based configuration, annotation-based configuration (e.g., @Component, @Autowired), or Java-based configuration (using @Configuration and @Bean annotations).

3. What is XML-based configuration in Spring?  
   It involves defining beans and their properties in an XML file, where the Spring container reads the metadata to instantiate and wire beans, such as using <bean> tags for definitions.

4. What is annotation-based configuration?  
   This uses annotations like @Component, @Service, @Repository, and @Autowired to enable component scanning and automatic dependency injection, reducing the need for XML.

5. What is Java-based configuration in Spring?  
   It uses Java classes annotated with @Configuration to define beans via @Bean methods, providing type-safe configuration without XML.

6. What is the role of the @Configuration annotation?  
   It indicates that a class declares one or more @Bean methods and can be processed by the Spring container to generate bean definitions and service requests.

7. How does autowiring work in Spring configuration?  
   Autowiring automatically injects dependencies into beans using modes like byName (matches bean names), byType (matches types), or constructor, configured via XML attributes or @Autowired.

8. What are the differences between @Component, @Service, @Repository, and @Controller?  
   All are stereotypes for component scanning; @Component is general, @Service for business logic, @Repository for data access (with exception translation), and @Controller for web MVC handlers.

9. How do you externalize configuration in Spring?  
   Use properties files with @PropertySource or application.properties, and inject values using @Value("${property.name}"). In Spring Boot, this is enhanced with auto-configuration.

10. What are Spring profiles?  
    Profiles allow environment-specific configurations (e.g., dev, prod) using @Profile or active profiles in properties, enabling conditional bean creation.

11. What is the Spring IoC container and its role in configuration?  
    The IoC container manages bean lifecycle and dependencies based on configuration metadata (XML, annotations, or Java), inverting control from the application to the framework.

12. How do you enable component scanning in Spring?  
    Use <context:component-scan base-package="com.example"/> in XML or @ComponentScan in Java config to automatically detect and register annotated classes as beans.

13. What is the difference between singleton and prototype bean scopes in configuration?  
    Singleton (default) creates one instance per container; prototype creates a new instance each time, configured via scope attribute in XML or @Scope annotation.

14. How do you handle inner beans in Spring configuration?  
    Inner beans are defined within another bean's property in XML (e.g., inside <property> or <constructor-arg>), useful for beans used only once.

15. What is lazy initialization in Spring configuration?  
    Beans are created only when first requested (not at startup) by setting lazy-init="true" in XML or @Lazy, useful for performance optimization.