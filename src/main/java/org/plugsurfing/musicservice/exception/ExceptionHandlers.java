package org.plugsurfing.musicservice.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {

    private static final Logger LOGGER = LogManager.getLogger(ExceptionHandlers.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String serverExceptionHandler(final Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ex.getMessage();
    }
}