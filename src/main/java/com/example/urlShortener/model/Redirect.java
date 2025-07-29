package com.example.urlShortener.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Data
@Table(name = "redirections")
@NoArgsConstructor
public class Redirect {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String shortenedUrl;

    @Column(unique = true)
    private String longUrl;

    @Column(unique = true)
    private String shortKey;

    private Date creationDate;

    private Long usages;

    public Redirect(String longUrl, String shortKey, String shortenedUrl) {
        this.longUrl = longUrl;
        this.shortKey = shortKey;
        this.shortenedUrl = shortenedUrl;
        this.creationDate = new Date();
        this.usages = 0L;
    }

    public void incrementUsages() {
        this.usages++;
    }
}
