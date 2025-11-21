package com.teqmonic.urlshortner.service;

import com.teqmonic.urlshortner.model.entities.ShortUrlEntity;
import com.teqmonic.urlshortner.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;

    public List<ShortUrlEntity> findPublicShortUrls() {
        return shortUrlRepository.findPublicShortUrls();
    }
}
