package com.teqmonic.urlshortner.service;

import com.teqmonic.urlshortner.model.entities.UserEntity;
import com.teqmonic.urlshortner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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
