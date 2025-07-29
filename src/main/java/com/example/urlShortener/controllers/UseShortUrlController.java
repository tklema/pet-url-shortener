package com.example.urlShortener.controllers;

import com.example.urlShortener.services.UrlShortenerService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UseShortUrlController extends ExceptionController {
    @Autowired
    private UrlShortenerService urlShortenerService;

    @GetMapping("/{shortKey}")
    public void redirect(@PathVariable String shortKey, HttpServletResponse response) {
        String longUrl = urlShortenerService.findLongUrlByShortKey(shortKey);
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", longUrl);
    }
}
