package com.example.urlShortener.controllers;

import com.example.urlShortener.model.ShortenedUrlStats;
import com.example.urlShortener.services.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class StatisticController extends ExceptionController {
    @Autowired
    private UrlShortenerService urlShortenerService;

    @GetMapping("stats/{shortKey}")
    public ShortenedUrlStats stats(@PathVariable String shortKey) {
        return urlShortenerService.getStats(shortKey);
    }
}
