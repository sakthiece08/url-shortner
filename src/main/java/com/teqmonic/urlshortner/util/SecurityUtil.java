package com.teqmonic.urlshortner.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(null != authentication &&  authentication.isAuthenticated()) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    public String getCurrentUserName() {
        User user = getCurrentUser();
        if(null != user) {
            return user.getUsername();
        }
        return null;
    }
}
