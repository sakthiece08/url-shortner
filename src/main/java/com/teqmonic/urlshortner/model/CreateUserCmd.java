package com.teqmonic.urlshortner.model;

public record CreateUserCmd(
        String email,
        String password,
        String name,
        Role role
) {
}
