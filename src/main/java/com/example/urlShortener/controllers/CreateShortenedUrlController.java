package com.example.urlShortener.controllers;

import com.example.urlShortener.services.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateShortenedUrlController {

    @Autowired
    UrlShortenerService urlShortenerService;

    @PostMapping("/shortened_url")
    public String shorten(@RequestBody String longUrl) {
        return urlShortenerService.shortenUrl(longUrl);
    }
}
