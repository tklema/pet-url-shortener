package com.example.urlShortener.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ShortenedUrlStats {
    private Long usages;
    private Date creationDate;
}
