package com.example.banking.domain;

import java.util.UUID;

public record TransactionId(String value) {
    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID().toString());
    }
}