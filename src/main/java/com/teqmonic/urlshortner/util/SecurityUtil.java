package com.teqmonic.urlshortner.util;

import com.teqmonic.urlshortner.model.entities.UserEntity;
import com.teqmonic.urlshortner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class SecurityUtil {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(null != authentication &&  authentication.isAuthenticated()) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    public String getCurrentUserName() {
        User user = getCurrentUser();
       return user!=null? user.getUsername():null;
    }

    public Long getCurrentUserId() {
        User user = getCurrentUser();
        Optional<UserEntity> userEntity = userRepository.findByName(user.getUsername());
        return userEntity.map(UserEntity::getId).orElse(null);
    }
}
