package com.example.urlShortener.model;

public class InvalidUrlException extends ModelException {
    public InvalidUrlException(String message) {
        super(message);
    }
}
