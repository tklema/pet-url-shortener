package com.example.urlShortener.controllers;

import com.example.urlShortener.model.AlreadyExistsException;
import com.example.urlShortener.model.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ExceptionController {
    @ExceptionHandler(NotFoundException.class)
    void handleNotFound(HttpServletResponse response, Exception exception) throws IOException {
        setResponse(response, HttpServletResponse.SC_NOT_FOUND, exception);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    void handleAlreadyExists(HttpServletResponse response, Exception exception) throws IOException {
        setResponse(response, HttpServletResponse.SC_BAD_REQUEST, exception);
    }

    private void setResponse(HttpServletResponse response, int status, Exception exception) throws IOException {
        response.setStatus(status);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()))) {
            bw.write(exception.getMessage());
        }
    }
}
