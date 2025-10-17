package com.swa.order_domain.entity;

/**
 * Base class for aggregate roots in the domain model.
 * An aggregate root is an entity that is the root of an aggregate.
 * 
 * @param <ID> The type of the entity's identifier
 */
public abstract class AggregateRoot<ID> extends BaseEntity<ID> {
    // You can add common aggregate root behavior here
    // For example, domain events support:
    // private List<DomainEvent> domainEvents = new ArrayList<>();
    
    // public void addDomainEvent(DomainEvent event) {
    //     domainEvents.add(event);
    // }
    // 
    // public void clearDomainEvents() {
    //     domainEvents.clear();
    // }
    // 
    // public List<DomainEvent> getDomainEvents() {
    //     return Collections.unmodifiableList(domainEvents);
    // }
}
