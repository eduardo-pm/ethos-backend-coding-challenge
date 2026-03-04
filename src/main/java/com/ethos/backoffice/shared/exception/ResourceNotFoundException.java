package com.ethos.backoffice.shared.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String entity, Object identifier) {
        super(String.format("%s not found with identifier: %s", entity, identifier));
    }
}
