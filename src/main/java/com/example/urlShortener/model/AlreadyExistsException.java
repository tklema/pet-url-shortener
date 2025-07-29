package com.example.urlShortener.model;

public class AlreadyExistsException extends ModelException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
