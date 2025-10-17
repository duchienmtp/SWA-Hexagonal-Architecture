package com.swa.order_container.rest;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.swa.order_application.dto.*;
import com.swa.order_application.ports.input.service.IOrderApplicationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/orders")
@Slf4j
public class OrderController {

    private final IOrderApplicationService _orderApplicationService;

    @GetMapping(value = "test")
    public ResponseEntity<Object> test() {
        return ResponseEntity.ok(Map.of("message", "Test", "status", 200));
    }

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CreateOrderResponse> createOrder(
            @Valid @RequestBody CreateOrderCommand createOrderCommand) {
        // Log request
        log.info("Creating order for customer: {} at restaurant: {}",
                createOrderCommand.getCustomerId(),
                createOrderCommand.getRestaurantId());

        // Delegate to use case (InputPort)
        CreateOrderResponse response = _orderApplicationService.createOrder(createOrderCommand);

        // Log success
        log.info("Order created with trackingId: {}",
                response.getOrderTrackingId());

        // Return HTTP 201 Created
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping(value = "/tracking-order", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TrackOrderResponse> trackOrder(@RequestBody @Valid TrackOrderQuery trackOrderQuery) {
        TrackOrderResponse response = _orderApplicationService.trackOrder(trackOrderQuery);

        log.info("Order tracked with trackingId: {}",
                response.getTrackingOrder().getTrackingId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping(value = "/cancel-order", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CancelOrderResponse> cancelOrder(@RequestBody @Valid CancelOrderCommand cancelOrderCommand) {
        CancelOrderResponse response = _orderApplicationService.cancelOrder(cancelOrderCommand);

        log.info("Order cancelled with trackingId: {}",
                response.getTrackingOrder().getTrackingId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
