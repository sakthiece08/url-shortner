package com.teqmonic.urlshortner.repository;

import com.teqmonic.urlshortner.model.Role;
import com.teqmonic.urlshortner.model.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
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
