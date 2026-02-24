package com.teqmonic.urlshortner.repository;

import com.teqmonic.urlshortner.model.Role;
import com.teqmonic.urlshortner.model.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@Slf4j
public class UserRepository //extends JpaRepository<UserEntity, Long>
{

    private final JdbcClient jdbcClient;
    public Optional<UserEntity> findByName(String username) {

         String sql = "SELECT id, email, password, name, role, created_at FROM users WHERE name = :name";
        return  jdbcClient
                   .sql(sql)
                   .param("name", username)
                   .query(new UserRowMapper())
                   .optional();
    }

    public void save(UserEntity user) {
        String sql = """
                INSERT INTO users (email, password, name, role, created_at)
                VALUES (:email, :password, :name, :role, :createdAt)
                RETURNING id
                """;
        var keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(sql)
                .param("email", user.getEmail())
                .param("password", user.getPassword())
                .param("name", user.getName())
                .param("role", user.getRole().name())
                .param("createdAt", Timestamp.from(user.getCreatedAt()))
                .update(keyHolder);
        Long userId = keyHolder.getKeyAs(Long.class);
        log.info("User saved with id: {}", userId);
    }


    static class UserRowMapper implements RowMapper<UserEntity> {

        @Override
        public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            var userEntity = new UserEntity();
            userEntity.setId(rs.getLong("id"));
            userEntity.setEmail(rs.getString("email"));
            userEntity.setPassword(rs.getString("password"));
            userEntity.setName(rs.getString("name"));
            userEntity.setRole(Role.valueOf(rs.getString("role")));
            userEntity.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            return userEntity;
        }
    }

}
