package com.example.urlShortener.controllers;

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
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()))) {
            bw.write(exception.getMessage());
        }
    }
}
