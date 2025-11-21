package com.teqmonic.urlshortner.repository;

import com.teqmonic.urlshortner.model.entities.ShortUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShortUrlRepository extends JpaRepository<ShortUrlEntity, Long> {

    //List<ShortUrlEntity> findByIsPrivateFalseOrderByCreatedAtDesc();

    @Query("select s from ShortUrlEntity s where s.isPrivate = false order by s.createdAt desc")
    List<ShortUrlEntity> findPublicShortUrls();

}
