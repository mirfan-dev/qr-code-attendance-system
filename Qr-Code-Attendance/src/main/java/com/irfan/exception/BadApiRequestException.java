package com.irfan.exception;


public class BadApiRequestException extends RuntimeException {

    public BadApiRequestException(String message) {
        super(message);
    }

    public BadApiRequestException() {
    }

}
