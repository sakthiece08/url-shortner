### Spring Boot URL Shortener   


A simple URL shortener application built with Spring Boot. This application allows users to shorten long URLs and redirect to the original URLs using the shortened versions.

Learnings:

* Learn how to handle web requests
* Using Thymeleaf to render HTML Templates with dynamic data 
* Bootstrap CSS framework for styling
* Create layout template
* Using WebJars for loading static resources
* Flyway DB migrations for database version control

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

#### Flyway DB migrations:
Add corresponding flyway dependency for the database in pom.xml
Add migration scripts in _**src/main/resources/db/migration**_
Script names as below:
* V1__Create_url_table.sql
* V2__Insert_data.sql

Move the existing schema.sql and data.sql to migration scripts.
Then Flyway will automatically run these scripts on application startup to set up the database schema and initial data. We can 
see the DB versions in the table _flyway_schema_history_


