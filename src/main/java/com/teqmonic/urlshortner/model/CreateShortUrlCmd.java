package com.teqmonic.urlshortner.model;

public record CreateShortUrlCmd(
        String originalUrl,
        Boolean isPrivate,
        Long expirationInDays,
        String userName) {
}
