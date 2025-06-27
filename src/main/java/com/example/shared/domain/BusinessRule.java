package com.example.shared.domain;

public interface BusinessRule {
    boolean isSatisfied();
    String getMessage();
}