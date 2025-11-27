package com.teqmonic.urlshortner.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtility {

    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("Encoded admin password: " + encoder.encode("admin"));
        System.out.println("Encoded secret password: " + encoder.encode("secret"));
    }
}
