package com.swa.order_application.ports.input.service;

import com.swa.order_application.dto.CancelOrderCommand;
import com.swa.order_application.dto.CancelOrderResponse;
import com.swa.order_application.dto.CreateOrderCommand;
import com.swa.order_application.dto.CreateOrderResponse;
import com.swa.order_application.dto.TrackOrderQuery;
import com.swa.order_application.dto.TrackOrderResponse;

public interface IOrderApplicationService {
    // UseCase: Create order
    CreateOrderResponse createOrder(CreateOrderCommand command);

    // UseCase: Track order
    TrackOrderResponse trackOrder(TrackOrderQuery query);

    // UseCase: Cancel order
    CancelOrderResponse cancelOrder(CancelOrderCommand command);
}
