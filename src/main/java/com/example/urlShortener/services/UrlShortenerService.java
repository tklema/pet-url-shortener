package com.example.urlShortener.services;

import com.example.urlShortener.model.Redirect;
import com.example.urlShortener.model.UrlShortenerRepository;
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

        if (repository.existsByLongUrl(longUrl)) {
            return repository.findByLongUrl(longUrl).get().getShortenedUrl(); // TODO проверка на несуществование
        }

        String shortKey = generateShortKey();
        String shortenedUrl = domainPart + shortKey;

        Redirect redirect = new Redirect(longUrl, shortKey, shortenedUrl);

        repository.save(redirect);

        return shortenedUrl;
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

    public String findLongUrl(String shortKey) {
        return repository.findByShortKey(shortKey)
                .orElseThrow(() -> new RuntimeException("URL not found"))
                .getLongUrl();
    }

    private void validateUrl(String url) {}
}
