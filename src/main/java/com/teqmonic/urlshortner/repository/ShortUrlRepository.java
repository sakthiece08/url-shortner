package com.teqmonic.urlshortner.repository;

import com.teqmonic.urlshortner.model.entities.ShortUrlEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrlEntity, Long> {

    //List<ShortUrlEntity> findByIsPrivateFalseOrderByCreatedAtDesc();

   @Query("select s from ShortUrlEntity s left join fetch s.createdBy where s.isPrivate = false ")
   //@Query("select s from ShortUrlEntity s  where s.isPrivate = false  order by s.createdAt desc")
   //@EntityGraph(attributePaths = {"createdBy"})
   Page<ShortUrlEntity> findPagedPublicShortUrls(Pageable pageable);

    boolean existsByShortKey(String shortKey);

    Optional<ShortUrlEntity> findByShortKey(String shortKey);
}
