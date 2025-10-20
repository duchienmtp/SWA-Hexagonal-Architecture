package com.swa.customer_application.ports.output.event;

import com.swa.customer_domain.entity.Customer;

public interface IEventPublisher {
    void publishCreateUserBalanceEvent(Customer customer);
}
