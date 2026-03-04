package com.ethos.backoffice.shared.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String entity, String field, Object value) {
        super(String.format("%s already exists with %s: %s", entity, field, value));
    }
}
