package com.swa.restaurant_application.ports.input;

import com.swa.restaurant_domain.event.OrderPrepare;
import com.swa.restaurant_domain.event.RestaurantInventoryRollbackEvent;

public interface IRestaurantApplicationService {
    void handleOrderPrepare(OrderPrepare event);
    void handleInventoryRollback(RestaurantInventoryRollbackEvent event);
}
