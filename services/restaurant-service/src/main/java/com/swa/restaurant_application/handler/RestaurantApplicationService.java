package com.swa.restaurant_application.handler;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.swa.restaurant_application.ports.input.IRestaurantApplicationService;
import com.swa.restaurant_application.ports.output.IEventPublisher;
import com.swa.restaurant_application.ports.output.IRestaurantRepository;
import com.swa.restaurant_domain.entity.Inventory;
import com.swa.restaurant_domain.entity.OrderItem;
import com.swa.restaurant_domain.entity.Restaurant;
import com.swa.restaurant_domain.event.OrderPrepare;
import com.swa.restaurant_domain.event.OrderPrepareEvent;
import com.swa.restaurant_domain.event.RestaurantInventoryRollbackEvent;
import com.swa.restaurant_domain.valueobject.OrderPrepareStatus;
import com.swa.restaurant_domain.valueobject.ProductId;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantApplicationService implements IRestaurantApplicationService {
    private final IRestaurantRepository _restaurantRepository;
    private final IEventPublisher _eventPublisher;

    @Override
    public void handleOrderPrepare(OrderPrepare order) {
        log.info("Handling order prepare for order ID: {}", order.getOrderId());
        OrderPrepareEvent orderPrepareEvent = processOrderPreparation(order);

        if (orderPrepareEvent.getOrderPrepareStatus() == OrderPrepareStatus.ORDER_PREPARE_SUCCESS) {
            _eventPublisher.publishPrepareOrderSuccess(order, orderPrepareEvent);
        } else {
            _eventPublisher.publishPrepareOrderFailure(order, orderPrepareEvent);
        }
    }

    @Transactional
    public OrderPrepareEvent processOrderPreparation(OrderPrepare order) {
        try {
            boolean isPrepared = true;

            Optional<Restaurant> restaurantOptional = _restaurantRepository.findRestaurantById(
                    order.getRestaurantId());

            if (restaurantOptional.isEmpty()) {
                log.error("Restaurant not found with ID: {}", order.getRestaurantId().getValue());
                return new OrderPrepareEvent(OrderPrepareStatus.ORDER_PREPARE_FAILED,
                        "Restaurant not found");
            }

            Restaurant restaurant = restaurantOptional.get();

            // Check each order item against available inventory
            for (OrderItem orderItem : order.getOrderItems()) {
                ProductId productId = orderItem.getProductId();
                int requestedQuantity = orderItem.getQuantity();

                Optional<Inventory> inventoryOptional = _restaurantRepository.findInventoryByRestaurantAndProduct(
                        restaurant.getId(), productId);

                if (inventoryOptional.isEmpty()) {
                    log.error("Product with ID: {} not available in restaurant: {}",
                            orderItem.getProductId().getValue(), order.getRestaurantId().getValue());
                    isPrepared = false;
                    break;
                }

                Inventory inventory = inventoryOptional.get();

                // Check if there's enough quantity available
                if (inventory.getQuantity() < requestedQuantity) {
                    log.error("Insufficient quantity for product: {}. Required: {}, Available: {}",
                            orderItem.getProductId(), requestedQuantity, inventory.getQuantity());
                    isPrepared = false;
                    break;
                }

                try {
                    inventory.decreaseQuantity(requestedQuantity);
                    _restaurantRepository.save(inventory);
                } catch (Exception e) {
                    log.error("Error updating inventory for product: {}: {}",
                            orderItem.getProductId(), e.getMessage());
                    isPrepared = false;
                    break;
                }
            }

            if (isPrepared) {
                log.info("Order prepared successfully for order ID: {}", order.getOrderId().getValue());
                return new OrderPrepareEvent(OrderPrepareStatus.ORDER_PREPARE_SUCCESS,
                        "Order prepared successfully");
            } else {
                log.error("Order preparation failed for order ID: {}", order.getOrderId().getValue());
                return new OrderPrepareEvent(OrderPrepareStatus.ORDER_PREPARE_FAILED,
                        "Insufficient inventory to prepare order");
            }
        } catch (Exception e) {
            log.error("Error during order preparation for order ID: {}: {}",
                    order.getOrderId().getValue(), e.getMessage(), e);
            return new OrderPrepareEvent(OrderPrepareStatus.ORDER_PREPARE_FAILED,
                    "Exception during order preparation: " + e.getMessage());
        }
    }

    @Override
    public void handleInventoryRollback(RestaurantInventoryRollbackEvent event) {
        log.info("Handling inventory rollback for order ID: {}", event.getOrderId());
        OrderPrepareEvent orderPrepareEvent = processInventoryRollback(event);
        _eventPublisher.publishPrepareOrderFailure(event, orderPrepareEvent);

    }

    @Transactional
    public OrderPrepareEvent processInventoryRollback(RestaurantInventoryRollbackEvent event) {
        try {
            Optional<Restaurant> restaurantOptional = _restaurantRepository.findRestaurantById(
                    event.getRestaurantId());

            if (restaurantOptional.isEmpty()) {
                log.error("Restaurant not found with ID: {}", event.getRestaurantId().getValue());
                return new OrderPrepareEvent(OrderPrepareStatus.ORDER_PREPARE_FAILED,
                        "Restaurant not found");
            }

            Restaurant restaurant = restaurantOptional.get();

            // Rollback each order item to the inventory
            for (OrderItem orderItem : event.getOrderItems()) {
                ProductId productId = orderItem.getProductId();
                int quantityToRollback = orderItem.getQuantity();

                Optional<Inventory> inventoryOptional = _restaurantRepository.findInventoryByRestaurantAndProduct(
                        restaurant.getId(), productId);

                if (inventoryOptional.isEmpty()) {
                    log.error("Product with ID: {} not found in restaurant: {} during rollback",
                            orderItem.getProductId(), event.getRestaurantId());
                    continue;
                }

                Inventory inventory = inventoryOptional.get();

                try {
                    inventory.increaseQuantity(quantityToRollback);
                    _restaurantRepository.save(inventory);
                    log.info("Rolled back {} units of product {} for restaurant {}",
                            quantityToRollback, productId.getValue(), restaurant.getId().getValue());
                } catch (Exception e) {
                    log.error("Error rolling back inventory for product: {}: {}",
                            orderItem.getProductId(), e.getMessage());
                }
            }

            log.info("Inventory rollback completed for order ID: {}", event.getOrderId());
            return new OrderPrepareEvent(OrderPrepareStatus.ORDER_PREPARE_FAILED,
                    event.getMessage());
        } catch (Exception e) {
            log.error("Error during inventory rollback for order ID: {}: {}",
                    event.getOrderId(), e.getMessage(), e);
            return new OrderPrepareEvent(OrderPrepareStatus.ORDER_PREPARE_FAILED,
                    "Error during inventory rollback: " + e.getMessage());
        }
    }
}
