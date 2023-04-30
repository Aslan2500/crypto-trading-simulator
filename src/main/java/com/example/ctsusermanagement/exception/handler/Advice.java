package com.example.ctsusermanagement.exception.handler;

import com.example.ctsusermanagement.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class Advice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleUserNotFound(UsernameNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleAuthenticationException(AuthenticationException e) {
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String handeUserAlreadyExistsException(UserAlreadyExistsException e) {
        return e.getMessage();
    }
}
