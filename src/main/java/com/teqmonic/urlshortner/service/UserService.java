package com.teqmonic.urlshortner.service;

import com.teqmonic.urlshortner.model.CreateUserCmd;
import com.teqmonic.urlshortner.model.entities.UserEntity;
import com.teqmonic.urlshortner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(CreateUserCmd cmd) {

        if(userRepository.findByName(cmd.name()).isPresent()) {
            throw new RuntimeException("User with name "+cmd.name()+" already exists");
        }

        var userEntity = new UserEntity();
        userEntity.setEmail(cmd.email());
        userEntity.setPassword(passwordEncoder.encode(cmd.password()));
        userEntity.setName(cmd.name());
        userEntity.setRole(cmd.role());
        userEntity.setCreatedAt(Instant.now());
        try {
            userRepository.save(userEntity);
        } catch (Exception e) {
            log.error("Error saving user entity: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user", e);
        }
    }



}
