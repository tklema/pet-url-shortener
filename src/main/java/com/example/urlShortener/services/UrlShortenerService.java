package com.example.urlShortener.services;

import com.example.urlShortener.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Random;

@Service
public class UrlShortenerService {
    @Autowired
    UrlShortenerRepository repository;

    @Value("${domainPart}")
    private String domainPart = "http://localhost:8080/";

    @Value("${shortKeySize}")
    private Integer shortKeySize = 5;

    @Value("${maxLiveTimeSeconds}")
    private Long maxLiveTimeSeconds = 86400L;

    public String shortenUrl(String longUrl) {
        validateUrl(longUrl);

        try {
            Redirect redirect = findRedirectByLongUrl(longUrl);
            if (redirectIsAlive(redirect)) {
                return redirect.getShortenedUrl();
            }
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
        try {
            Redirect redirect = findRedirectByShortKey(shortKey);
            if (redirectIsAlive(redirect)) {
                throw new AlreadyExistsException("this custom key already exists");
            }
        } catch (NotFoundException ignore) {
            // ignored
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

    public String useShortKey(String shortKey) {
        Redirect redirect = findRedirectByShortKey(shortKey);

        if (!redirectIsAlive(redirect)) {
            throw new NotFoundException(String.format("redirect not found by shortKey: %s", shortKey));
        }

        redirect.incrementUsages();

        repository.save(redirect);

        return redirect.getLongUrl();
    }

    private void validateUrl(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            if (200 > responseCode || responseCode >= 400) {
                throw new InvalidUrlException(String.format("invalid url: %s", url));
            }
        } catch (IOException e) {
            throw new InvalidUrlException(String.format("invalid url: %s", url));
        }
    }

    private boolean redirectIsAlive(Redirect redirect) {
        long liveTime = (new Date().getTime() - redirect.getCreationDate().getTime()) / 1000;
        return liveTime <= maxLiveTimeSeconds;
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
