package com.example.exception;

public class UnknownException extends RuntimeException{

    public UnknownException(String message) {
        super(message);
    }

}
