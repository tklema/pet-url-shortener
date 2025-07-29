package com.example.urlShortener.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlShortenerRepository extends JpaRepository<Redirect, String> {
    Optional<Redirect> findByShortKey(String shortKey);
    Optional<Redirect> findByLongUrl(String longUrl);
    boolean existsByShortKey(String shortKey);
    boolean existsByLongUrl(String longUrl);
}
