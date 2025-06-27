package com.example.shared.domain;

import java.util.Objects;

public abstract class Entity<T> {
    private final T id;

    protected Entity(T id) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
    }

    public T getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}