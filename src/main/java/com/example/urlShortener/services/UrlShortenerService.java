package com.example.urlShortener.services;

import com.example.urlShortener.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UrlShortenerService {
    @Autowired
    UrlShortenerRepository repository;

    @Value("${domainPart}")
    private String domainPart = "http://localhost:8080/";

    @Value("${shortKeySize}")
    private Integer shortKeySize = 5;

    public String shortenUrl(String longUrl) {
        validateUrl(longUrl);

        try {
            return findRedirectByLongUrl(longUrl).getShortenedUrl();
        } catch (NotFoundException ignore) {
            // ignored
        }

        String shortKey = generateShortKey();
        String shortenedUrl = domainPart + shortKey;

        Redirect redirect = new Redirect(longUrl, shortKey, shortenedUrl);

        repository.save(redirect);

        return shortenedUrl;
    }

    public String shortenUrlCustom(ShortenerCustom shortenerCustom) {
        String longUrl = shortenerCustom.getLongUrl();
        validateUrl(longUrl);

        String shortKey = shortenerCustom.getCustomKey();
        if (repository.existsByShortKey(shortKey)) {
            throw new AlreadyExistsException("this custom key already exists");
        }

        String shortenedUrl = domainPart + shortKey;

        Redirect redirect = new Redirect(longUrl, shortKey, shortenedUrl);

        repository.save(redirect);

        return shortenedUrl;
    }

    private Redirect findRedirectByLongUrl(String longUrl) {
        return repository.findByLongUrl(longUrl).orElseThrow(() ->
                new NotFoundException(String.format("redirect not found by longUrl: %s", longUrl)));
    }

    private Redirect findRedirectByShortKey(String shortKey) {
        return repository.findByShortKey(shortKey).orElseThrow(() ->
                new NotFoundException(String.format("redirect not found by shortKey: %s", shortKey)));
    }

    public String findLongUrlByShortKey(String shortKey) {
        validateShortKey(shortKey);

        Redirect redirect = repository.findByShortKey(shortKey)
                .orElseThrow(() -> new RuntimeException("URL not found"));
        redirect.incrementUsages();

        repository.save(redirect);

        return redirect.getLongUrl();
    }

    private void validateUrl(String url) {
    }

    private void validateShortKey(String shortKey) {
    }

    public ShortenedUrlStats getStatistic(String shortKey) {
        Redirect redirect = findRedirectByShortKey(shortKey);
        return new ShortenedUrlStats(redirect.getUsages(), redirect.getCreationDate());
    }

    private String generateShortKey() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(shortKeySize);
        for (int i = 0; i < shortKeySize; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }
}
