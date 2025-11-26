package com.teqmonic.urlshortner.service;

import com.teqmonic.urlshortner.model.ShortUrlDto;
import com.teqmonic.urlshortner.model.UserDto;
import com.teqmonic.urlshortner.model.entities.ShortUrlEntity;
import com.teqmonic.urlshortner.model.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public ShortUrlDto toShortUrlDto(ShortUrlEntity shortUrl) {
        if (shortUrl == null) {
            return null;
        }
        return new ShortUrlDto(
                shortUrl.getId(),
                shortUrl.getShortKey(),
                shortUrl.getOriginalUrl(),
                shortUrl.getIsPrivate(),
                shortUrl.getExpiresAt(),
                toUserDto(shortUrl.getCreatedBy()),
                shortUrl.getClickCount(),
                shortUrl.getCreatedAt()
        );
    }

    public UserDto toUserDto(UserEntity user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getName());
    }
}
