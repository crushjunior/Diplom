package ru.skypro.homework.exception;

import org.springframework.security.access.AccessDeniedException;

public class AccessException extends AccessDeniedException {

    public AccessException(String message) {
        super(message);
    }
}
