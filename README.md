### Spring Boot URL Shortener   


A simple URL shortener application built with Spring Boot. This application allows users to shorten long URLs and redirect to the original URLs using the shortened versions.

Learnings:

* Learn how to handle web requests
* Using Thymeleaf to render HTML Templates with dynamic data 
* Bootstrap CSS framework for styling
* Create layout template
* Using WebJars for loading static resources
* Flyway DB migrations for database version control
* Docker Compose Support
* N+1 select problem and solution using Fetch Join
* Spring Boot Security
* Pagination using Spring Data JPA

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

#### Docker Compose Support:
Spring Boot automatically detects various external dependencies using pom.xml and adds the required  container definition in the docker-compose.yml file.
No need to manually create service definitions for databases like MySQL, PostgreSQL, etc. in application.properties file.

Also, when we start the application, containers defined in the compose file also start automatically. It will stop the containers when the application stops.
We can override this using below configuration in application.properties file:
```
spring.docker.compose.lifecycle-management=start_only
```
Note: This is useful only for **local development purpose**. The connection configs mentioned in application.properties are overridden with values defined in the docker file.

#### N+1 select problem and solution using Fetch Join:
N+1 select problem occurs when an application needs to load a collection of entities and for each entity, it needs to load a related collection. This results in one query to load the main entities (N) and then N additional queries to load the related collections, leading to performance issues.

To solve this problem, we can use Fetch Join in JPQL or Criteria API to load the related entities in a single query. This reduces the number of queries executed and improves performance.
```
@Query("select s from ShortUrlEntity s join fetch s.createdBy where s.isPrivate = false  order by s.createdAt desc")
```
Alternatively, we can use Entity Graphs along with initial query to specify which related entities should be loaded eagerly.
```
@Query("select s from ShortUrlEntity s  where s.isPrivate = false  order by s.createdAt desc")
@EntityGraph(attributePaths = {"createdBy"})
```
Also set spring.jpa.open-in-view=false in application.properties to avoid lazy loading outside transaction scope.
This ensures that all necessary data is loaded within the transaction, preventing additional queries during view rendering.

#### Spring Boot Security:

Add spring-boot-starter-security dependency in pom.xml
Configure security settings in application.properties

_**Note:** Only for Local dev purpose, we can set default user credentials in application.properties_
```
spring.security.user.name=sakthi
spring.security.user.password=sakthi
spring.security.user.roles=USER, ADMIN
```
With the above configurations Spring Security will display a default login page when hitting http://localhost:8080
But we need to customize the authorizations where we want to allow few endpoints without authentication and few are protected.

Create a SecurityConfig class to customize the security settings:
```
@Component
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Auth runs from top to bottom. First match applies. Default rule is always protected.
        http.csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(
                                "/error", "/webjars/**", "/css/**", "/js/**", "/images/**",
                                "/", "/short-urls", "/s/**", "/register", "/login")
                                .permitAll()
                                .requestMatchers("/my-urls").authenticated()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }
}
```
#### Fetch User credentials from Database

Implement UserDetailsService interface to load user-specific data.
```
@RequiredArgsConstructor
@Service
public class SecurityUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByName(username).orElseThrow(() ->
                new UsernameNotFoundException("User with name " + username + " not found"));

        return new User(
                userEntity.getName(),
                userEntity.getPassword(),
                List.of(new SimpleGrantedAuthority(userEntity.getRole().name()))
        );
    }
}
```
* When a user tries to log in, Spring Security will use this service to fetch user details from the database and authenticate the user accordingly.
* No need to configure this service explicitly as Spring Boot auto-detects the implementation of UserDetailsService and uses it for authentication.

Refer PasswordUtility class to hash the password before saving it to the database. User will login using plain text password which will be matched with the hashed password stored in the database.

We can get authenticated user details (Principle) using:
``` 
Authentication authentication =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
 ```
#### Pagination using Spring Data JPA

Spring Data JPA provides built-in support for pagination through the Pageable interface and Page<T> return type.

```
Interface: JpaRepository extends ListPagingAndSortingRepository extends PagingAndSortingRepository : Page<T> findAll(Pageable pageable);

  @Query("select s from ShortUrlEntity s left join fetch s.createdBy where s.isPrivate = false ")
   Page<ShortUrlEntity> findPagedPublicShortUrls(Pageable pageable);
```
This will internally generate 2 queries to get  data and to get  total count for pagination.
```
Hibernate: select sue1_0.id,sue1_0.click_count,sue1_0.created_at,sue1_0.created_by,cb1_0.id,cb1_0.created_at,cb1_0.email,cb1_0.name,cb1_0.password,cb1_0.role,sue1_0.expires_at,sue1_0.is_private,sue1_0.original_url,sue1_0.short_key from short_urls sue1_0 left join users cb1_0 on cb1_0.id=sue1_0.created_by where sue1_0.is_private=false 
order by sue1_0.created_at fetch first ? rows only
Hibernate: select count(*) from short_urls sue1_0 where sue1_0.is_private=false
```
* First query to fetch data will have: **fetch first ? rows only**
* Subsequent queries to fetch data : **offset ? rows fetch first ? rows only**