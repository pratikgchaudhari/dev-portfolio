---
title: Java Database Connectivity Sans Framework
date: 2025-09-02
summary: The steps for connecting to a database, running parameterized queries, and closing JDBC resources.
tag: Java
draft: false
---

1. **Load JDBC Driver**: Use `Class.forName("driver_class_name")` to load the database driver.
2. **Establish Connection**: Call `DriverManager.getConnection(url, username, password)` to connect to the database.
3. **Create PreparedStatement**: Use `Connection.prepareStatement(sql)` to create a prepared statement with parameterized queries.
4. **Set Parameters**: Use `PreparedStatement.setType(index, value)` to bind values to query parameters.
5. **Execute Query**: Use `PreparedStatement.executeQuery()` or `executeUpdate()` to run the parameterized query.
6. **Process Results**: For queries, use `ResultSet` to retrieve and process data.
7. **Close Resources**: Call `close()` on `ResultSet`, `PreparedStatement`, and `Connection` to free resources.