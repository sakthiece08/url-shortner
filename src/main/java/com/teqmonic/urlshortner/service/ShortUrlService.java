package com.teqmonic.urlshortner.service;

import com.teqmonic.urlshortner.configs.ApplicationProperties;
import com.teqmonic.urlshortner.model.CreateShortUrlCmd;
import com.teqmonic.urlshortner.model.PagedResult;
import com.teqmonic.urlshortner.model.ShortUrlDto;
import com.teqmonic.urlshortner.model.entities.ShortUrlEntity;
import com.teqmonic.urlshortner.repository.ShortUrlRepository;
import com.teqmonic.urlshortner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ShortUrlService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_KEY_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;
    private final ApplicationProperties properties;

    public PagedResult<ShortUrlDto> findAllPublicShortUrls(int pageNo) {
        pageNo = pageNo > 1 ? pageNo - 1 : 0;
        // In Spring data, page number is zero based
        Pageable pageable = PageRequest.of(pageNo, properties.homePageShortUrlLimit(), Sort.by(Sort.Direction.ASC, "createdAt"));
        //Page<ShortUrlEntity> shortUrlEntities = shortUrlRepository.findAll(pageable);
        Page<ShortUrlDto> shortUrlDtoPage = shortUrlRepository.findPagedPublicShortUrls(pageable).map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlDtoPage);
    }

    @Transactional // overwrite readOnly as false
    public ShortUrlDto createShortUrl(CreateShortUrlCmd cmd) {
        if(properties.validateOriginalUrl()) {
            boolean urlExists = UrlExistenceValidator.isUrlExists(cmd.originalUrl());
            if(!urlExists) {
                throw new RuntimeException("Invalid URL "+cmd.originalUrl());
            }
        }
        var shortKey = generateUniqueShortKey();
        var shortUrl = new ShortUrlEntity();
        shortUrl.setOriginalUrl(cmd.originalUrl());
        shortUrl.setShortKey(shortKey);
        if(cmd.userName() == null) {
            shortUrl.setCreatedBy(null);
            shortUrl.setIsPrivate(false);
            shortUrl.setExpiresAt(Instant.now().plus(properties.defaultExpiryInDays(), DAYS));
        } else {
            shortUrl.setCreatedBy(userRepository.findByName(cmd.userName()).orElseThrow());
            shortUrl.setIsPrivate(cmd.isPrivate());
            shortUrl.setExpiresAt(cmd.expirationInDays() != null ?  Instant.now().plus(cmd.expirationInDays(), DAYS) : null);
        }
        shortUrl.setClickCount(0L);
        shortUrl.setCreatedAt(Instant.now());
        shortUrlRepository.save(shortUrl);
        return entityMapper.toShortUrlDto(shortUrl);
    }

    private String generateUniqueShortKey() {
        String shortKey;
        do {
            shortKey = generateRandomShortKey();
        } while (shortUrlRepository.existsByShortKey(shortKey));
        return shortKey;
    }

    public static String generateRandomShortKey() {
        StringBuilder sb = new StringBuilder(SHORT_KEY_LENGTH);
        for (int i = 0; i < SHORT_KEY_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    @Transactional
    public Optional<ShortUrlDto> accessShortUrl(String shortKey, String userName) {
        Optional<ShortUrlEntity> shortUrlOptional = shortUrlRepository.findByShortKey(shortKey);
        if(shortUrlOptional.isEmpty()) {
            return Optional.empty();
        }
        ShortUrlEntity shortUrl = shortUrlOptional.get();
        if(shortUrl.getExpiresAt() != null && shortUrl.getExpiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }

        if(shortUrl.getIsPrivate() != null && shortUrl.getIsPrivate() && shortUrl.getCreatedBy() != null &&
                !shortUrl.getCreatedBy().getName().equalsIgnoreCase(userName)) {
            return Optional.empty();
        }
        shortUrl.setClickCount(shortUrl.getClickCount()+1);
        shortUrlRepository.save(shortUrl);
        return shortUrlOptional.map(entityMapper::toShortUrlDto);
    }

}
