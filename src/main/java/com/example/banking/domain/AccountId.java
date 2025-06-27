package com.example.banking.domain;

import java.util.UUID;

public record AccountId(String value) {
    public static AccountId generate() {
        return new AccountId(UUID.randomUUID().toString());
    }
    
    public static AccountId of(String value) {
        return new AccountId(value);
    }
}