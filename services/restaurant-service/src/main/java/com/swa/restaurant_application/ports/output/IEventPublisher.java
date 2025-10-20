package com.swa.restaurant_application.ports.output;

import com.swa.restaurant_domain.event.OrderPrepare;
import com.swa.restaurant_domain.event.OrderPrepareEvent;
import com.swa.restaurant_domain.event.RestaurantInventoryRollbackEvent;

public interface IEventPublisher {
    void publishPrepareOrderSuccess(OrderPrepare order, OrderPrepareEvent orderPrepareEvent);
    void publishPrepareOrderFailure(OrderPrepare order, OrderPrepareEvent orderPrepareEvent);
    void publishPrepareOrderFailure(RestaurantInventoryRollbackEvent order, OrderPrepareEvent orderPrepareEvent);
}
