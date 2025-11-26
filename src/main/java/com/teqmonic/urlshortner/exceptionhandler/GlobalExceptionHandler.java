package com.teqmonic.urlshortner.exceptionhandler;

import com.teqmonic.urlshortner.exception.ShortUrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShortUrlNotFoundException.class)
    public String handleException(ShortUrlNotFoundException ex) {
       log.error("Short URL not found: {}", ex.getMessage());
       return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex) {
       log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
       return "error/500";
    }
}
