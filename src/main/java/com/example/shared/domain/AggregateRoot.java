package com.example.shared.domain;

import java.time.LocalDateTime;

/**
 * Base class for aggregate roots that provides:
 * 1. Version control for optimistic locking
 * 2. Aggregate boundary enforcement
 * 3. Business rule validation framework
 * 
 * Note: Domain events are handled via Spring's ApplicationEventPublisher
 * in application services, not stored in aggregates.
 */
public abstract class AggregateRoot<T> extends Entity<T> {
    private Long version = 0L;
    private LocalDateTime lastModified;
    
    protected AggregateRoot(T id) {
        super(id);
        this.lastModified = LocalDateTime.now();
    }

    // Version Control for Optimistic Locking
    public final Long getVersion() {
        return version;
    }

    public final void setVersion(Long version) {
        this.version = version;
    }

    public final LocalDateTime getLastModified() {
        return lastModified;
    }

    protected final void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    // Business Rule Validation Framework
    protected final void checkBusinessRule(BusinessRule rule) {
        if (!rule.isSatisfied()) {
            throw new BusinessRuleViolationException(rule.getMessage());
        }
    }

    // Aggregate Boundary Protection
    protected final void markAsModified() {
        this.lastModified = LocalDateTime.now();
        this.version++;
    }

    // Template method for aggregate-specific validation
    protected abstract void validateAggregateInvariants();

    // Called before any state change to ensure aggregate consistency
    protected final void beforeStateChange() {
        validateAggregateInvariants();
        markAsModified();
    }
}