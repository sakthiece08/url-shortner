package com.teqmonic.urlshortner.service;

import com.teqmonic.urlshortner.model.entities.UserEntity;
import com.teqmonic.urlshortner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SecurityUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByName(username).orElseThrow(() ->
                new UsernameNotFoundException("User with name " + username + " not found"));
      log.info("User found: {}", userEntity.getName());
        return new User(
                userEntity.getName(),
                userEntity.getPassword(),
                List.of(new SimpleGrantedAuthority(userEntity.getRole().name()))
        );
    }
}
