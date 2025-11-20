### Spring Boot URL Shortener   


A simple URL shortener application built with Spring Boot. This application allows users to shorten long URLs and redirect to the original URLs using the shortened versions.

Learnings:

* Learn how to handle web requests
* Using Thymeleaf to render HTML Templates with dynamic data 
* Bootstrap CSS framework for styling
* Create layout template
* Using WebJars for loading static resources

Thymeleaf layout template:
```
<html lang="en"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout}">
```
@RestController = @Controller + @ResponseBody

```
spring.sql.init.mode=always
To initialize the database on startup for schema.sql and data.sql (Only for local development)
```


