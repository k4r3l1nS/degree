package com.practice.demo.exceptions.models;

public class AccountNameAlreadyTakenException extends RuntimeException {

    public AccountNameAlreadyTakenException(String message) {
        super(message);
    }
}
