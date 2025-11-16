package com.swa.order_application.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.swa.order_application.dto.CancelOrderCommand;
import com.swa.order_application.dto.CancelOrderResponse;
import com.swa.order_application.dto.CreateOrderCommand;
import com.swa.order_application.dto.CreateOrderItemDTO;
import com.swa.order_application.dto.CreateOrderResponse;
import com.swa.order_application.dto.CreateStreetAddressDTO;
import com.swa.order_application.dto.OrderItemDTO;
import com.swa.order_application.dto.QueryCustomerResponse;
import com.swa.order_application.dto.TrackOrderQuery;
import com.swa.order_application.dto.TrackOrderResponse;
import com.swa.order_application.dto.TrackingOrderDTO;
import com.swa.order_application.mapper.OrderDataMapper;
import com.swa.order_application.ports.output.event.IEventPublisher;
import com.swa.order_application.ports.output.repository.IOrderRepository;
import com.swa.order_application.ports.output.service.ICustomerFeignService;
import com.swa.order_domain.entity.Customer;
import com.swa.order_domain.entity.Order;
import com.swa.order_domain.entity.OrderItem;
import com.swa.order_domain.event.OrderApprovalEvent;
import com.swa.order_domain.event.ProcessPaymentFailedEvent;
import com.swa.order_domain.exception.OrderDomainException;
import com.swa.order_domain.service.OrderDomainService;
import com.swa.order_domain.valueobject.CustomerId;
import com.swa.order_domain.valueobject.Money;
import com.swa.order_domain.valueobject.OrderId;
import com.swa.order_domain.valueobject.OrderStatus;
import com.swa.order_domain.valueobject.ProductId;
import com.swa.order_domain.valueobject.RestaurantId;
import com.swa.order_domain.valueobject.StreetAddress;
import com.swa.order_domain.valueobject.TrackingId;
import com.swa.order_infrastructure.order_messaging.producer.OrderRabbitMQPublisher;

/**
 * OrderApplicationService Unit Tests với @Nested để phân loại test cases theo
 * chức năng
 * 
 * Cấu trúc test:
 * - CreateOrderTests: Kiểm tra tạo đơn hàng
 * - TrackOrderTests: Kiểm tra tra cứu đơn hàng
 * - CancelOrderTests: Kiểm tra huỷ đơn hàng
 * - ApproveOrderTests: Kiểm tra duyệt đơn hàng
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderApplicationService Unit Tests")
class OrderApplicationServiceNestedTest {

    @Mock
    private IOrderRepository orderRepository;
    @Mock
    private OrderDataMapper orderDataMapper;
    @Mock
    private OrderDomainService orderDomainService;
    @Mock
    private ICustomerFeignService customerClient;
    @Mock
    private OrderRabbitMQPublisher orderRabbitMQPublisher;
    @Mock
    private IEventPublisher eventPublisher;

    @InjectMocks
    private OrderApplicationService orderApplicationService;

    // ===== Nested Test Classes =====

    @Nested
    @DisplayName("CreateOrder Scenarios")
    class CreateOrderTests {

        @Test
        @DisplayName("publishes events when customer exists and order is valid")
        void shouldReturnResponseAndPublishEvents() {
            // Arrange
            CreateOrderCommand command = buildCreateOrderCommand();
            Customer customer = buildCustomer(command.getCustomerId());
            QueryCustomerResponse customerResponse = buildCustomerResponse(customer);
            Order order = buildOrder(command.getCustomerId(), command.getRestaurantId());
            CreateOrderResponse expectedResponse = CreateOrderResponse.builder()
                    .orderTrackingId(order.getTrackingId().getValue())
                    .message("Order created successfully")
                    .status("201")
                    .build();

            when(customerClient.findCustomerById(command.getCustomerId().toString()))
                    .thenReturn(Optional.of(customerResponse));
            when(orderDataMapper.toOrder(command)).thenReturn(order);
            when(orderDomainService.validateAndInitializeOrder(order)).thenReturn(order);
            when(orderRepository.save(order)).thenReturn(order);
            when(orderDataMapper.toCreateOrderResponseDTO(order, "Order created successfully", 201))
                    .thenReturn(expectedResponse);

            // Act
            CreateOrderResponse actualResponse = orderApplicationService.createOrder(command);

            // Assert
            assertSame(expectedResponse, actualResponse);
            verify(eventPublisher, times(1)).sendOrderConfirmationEvent(order, customer);
            verify(eventPublisher, times(1)).sendOrderPurchaseEvent(order, customer);
        }

        @Test
        @DisplayName("throws when customer cannot be resolved")
        void shouldThrowWhenCustomerNotFound() {
            // Arrange
            CreateOrderCommand command = buildCreateOrderCommand();
            when(customerClient.findCustomerById(command.getCustomerId().toString()))
                    .thenReturn(Optional.empty());

            // Act & Assert
            OrderDomainException exception = assertThrows(OrderDomainException.class,
                    () -> orderApplicationService.createOrder(command));

            assertEquals("Failed to create order", exception.getMessage());
            verifyNoInteractions(orderDataMapper, orderDomainService, orderRepository, eventPublisher);
        }
    }

    @Nested
    @DisplayName("TrackOrder Scenarios")
    class TrackOrderTests {

        @Test
        @DisplayName("returns order data when order and customer exist")
        void shouldReturnResponse() {
            // Arrange
            TrackOrderQuery query = buildTrackOrderQuery();
            Customer customer = buildCustomer(query.getCustomerId());
            QueryCustomerResponse customerResponse = buildCustomerResponse(customer);
            Order order = buildOrder(query.getCustomerId(), UUID.randomUUID());
            order.setId(OrderId.of(query.getOrderId()));
            TrackingOrderDTO trackingOrder = buildTrackingDto(query);
            TrackOrderResponse expectedResponse = TrackOrderResponse.builder()
                    .trackingOrder(trackingOrder)
                    .message("Order tracked successfully")
                    .status("200")
                    .build();

            when(customerClient.findCustomerById(query.getCustomerId().toString()))
                    .thenReturn(Optional.of(customerResponse));
            when(orderRepository.findById(OrderId.of(query.getOrderId()))).thenReturn(Optional.of(order));
            when(orderDataMapper.toTrackingOrderDTO(order)).thenReturn(trackingOrder);
            when(orderDataMapper.toTrackOrderResponseDTO(trackingOrder, "Order tracked successfully", 200))
                    .thenReturn(expectedResponse);

            // Act
            TrackOrderResponse actualResponse = orderApplicationService.trackOrder(query);

            // Assert
            assertSame(expectedResponse, actualResponse);
        }

        @Test
        @DisplayName("throws when customer is missing")
        void shouldThrowWhenCustomerNotFound() {
            // Arrange
            TrackOrderQuery query = buildTrackOrderQuery();
            when(customerClient.findCustomerById(query.getCustomerId().toString()))
                    .thenReturn(Optional.empty());

            // Act & Assert
            OrderDomainException exception = assertThrows(OrderDomainException.class,
                    () -> orderApplicationService.trackOrder(query));

            assertEquals("Failed to retrieve order", exception.getMessage());
            verify(orderRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("CancelOrder Scenarios")
    class CancelOrderTests {

        @Nested
        @DisplayName("CancelOrder Command (Sync)")
        class CancelOrderCommandTests {

            @Test
            @DisplayName("returns response when customer exists")
            void shouldReturnResponse() {
                // Arrange
                CancelOrderCommand command = buildCancelOrderCommand();
                Customer customer = buildCustomer(command.getCustomerId());
                QueryCustomerResponse customerResponse = buildCustomerResponse(customer);
                Order order = buildOrder(command.getCustomerId(), UUID.randomUUID());
                order.setId(OrderId.of(command.getOrderId()));
                TrackingOrderDTO trackingOrder = buildTrackingDto(command.getOrderId(), command.getCustomerId());
                CancelOrderResponse expectedResponse = CancelOrderResponse.builder()
                        .trackingOrder(trackingOrder)
                        .message("Order cancelled successfully")
                        .status("200")
                        .build();

                when(customerClient.findCustomerById(command.getCustomerId().toString()))
                        .thenReturn(Optional.of(customerResponse));
                when(orderRepository.cancelOrder(OrderId.of(command.getOrderId()))).thenReturn(order);
                when(orderDataMapper.toTrackingOrderDTO(order)).thenReturn(trackingOrder);
                when(orderDataMapper.toCancelOrderResponseDTO(trackingOrder, "Order cancelled successfully", 200))
                        .thenReturn(expectedResponse);

                // Act
                CancelOrderResponse actualResponse = orderApplicationService.cancelOrder(command);

                // Assert
                assertSame(expectedResponse, actualResponse);
            }

            @Test
            @DisplayName("throws when customer is missing")
            void shouldThrowWhenCustomerNotFound() {
                // Arrange
                CancelOrderCommand command = buildCancelOrderCommand();
                when(customerClient.findCustomerById(command.getCustomerId().toString()))
                        .thenReturn(Optional.empty());

                // Act & Assert
                OrderDomainException exception = assertThrows(OrderDomainException.class,
                        () -> orderApplicationService.cancelOrder(command));

                assertEquals("Failed to cancel order", exception.getMessage());
                verify(orderRepository, never()).cancelOrder(any(OrderId.class));
            }
        }

        @Nested
        @DisplayName("CancelOrder Event (Async)")
        class CancelOrderEventTests {

            @Test
            @DisplayName("delegates to repository when customer exists")
            void shouldInvokeRepository() {
                // Arrange
                ProcessPaymentFailedEvent event = ProcessPaymentFailedEvent.builder()
                        .orderId(OrderId.of(UUID.randomUUID()))
                        .customerId(CustomerId.of(UUID.randomUUID()))
                        .message("payment failed")
                        .build();
                Customer customer = buildCustomer(event.getCustomerId().getValue());
                QueryCustomerResponse customerResponse = buildCustomerResponse(customer);

                when(customerClient.findCustomerById(event.getCustomerId().getValue().toString()))
                        .thenReturn(Optional.of(customerResponse));

                // Act
                orderApplicationService.cancelOrder(event);

                // Assert
                verify(orderRepository, times(1)).cancelOrder(event);
            }

            @Test
            @DisplayName("throws when customer missing")
            void shouldThrowWhenCustomerNotFound() {
                // Arrange
                ProcessPaymentFailedEvent event = ProcessPaymentFailedEvent.builder()
                        .orderId(OrderId.of(UUID.randomUUID()))
                        .customerId(CustomerId.of(UUID.randomUUID()))
                        .message("payment failed")
                        .build();

                when(customerClient.findCustomerById(event.getCustomerId().getValue().toString()))
                        .thenReturn(Optional.empty());

                // Act & Assert
                OrderDomainException exception = assertThrows(OrderDomainException.class,
                        () -> orderApplicationService.cancelOrder(event));

                assertEquals("Failed to cancel order", exception.getMessage());
                verify(orderRepository, never()).cancelOrder(event);
            }
        }
    }

    @Nested
    @DisplayName("ApproveOrder Scenarios")
    class ApproveOrderTests {

        @Test
        @DisplayName("approves when order and customer exist")
        void shouldApprove() {
            // Arrange
            OrderApprovalEvent event = OrderApprovalEvent.builder()
                    .orderId(OrderId.of(UUID.randomUUID()))
                    .customerId(CustomerId.of(UUID.randomUUID()))
                    .message("approve")
                    .build();
            Order order = buildOrder(event.getCustomerId().getValue(), UUID.randomUUID());
            order.setId(event.getOrderId());
            Customer customer = buildCustomer(event.getCustomerId().getValue());
            QueryCustomerResponse customerResponse = buildCustomerResponse(customer);

            when(orderRepository.findById(event.getOrderId())).thenReturn(Optional.of(order));
            when(customerClient.findCustomerById(event.getCustomerId().getValue().toString()))
                    .thenReturn(Optional.of(customerResponse));

            // Act
            orderApplicationService.approveOrder(event);

            // Assert
            verify(orderRepository, times(1)).approveOrder(event);
            verify(eventPublisher, never()).sendRestaurantInventoryRollbackEvent(any(Order.class),
                    any(CustomerId.class),
                    anyString());
        }

        @Test
        @DisplayName("triggers rollback when customer lookup fails")
        void shouldPublishRollbackWhenCustomerMissing() {
            // Arrange
            OrderApprovalEvent event = OrderApprovalEvent.builder()
                    .orderId(OrderId.of(UUID.randomUUID()))
                    .customerId(CustomerId.of(UUID.randomUUID()))
                    .message("approve")
                    .build();
            Order order = buildOrder(event.getCustomerId().getValue(), UUID.randomUUID());
            order.setId(event.getOrderId());

            when(orderRepository.findById(event.getOrderId())).thenReturn(Optional.of(order));
            when(customerClient.findCustomerById(event.getCustomerId().getValue().toString()))
                    .thenReturn(Optional.empty());

            // Act
            orderApplicationService.approveOrder(event);

            // Assert
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
            verify(eventPublisher, times(1)).sendRestaurantInventoryRollbackEvent(eq(order), eq(event.getCustomerId()),
                    messageCaptor.capture());
            verify(orderRepository, never()).approveOrder(event);
            assertEquals("No customer exists with ID " + event.getCustomerId().getValue(), messageCaptor.getValue());
        }
    }

    // ===== Test Fixtures & Builders =====

    private CreateOrderCommand buildCreateOrderCommand() {
        CreateStreetAddressDTO address = new CreateStreetAddressDTO();
        address.setStreet("123 Main");
        address.setPostalCode("10000");
        address.setCity("Hanoi");

        CreateOrderItemDTO item = new CreateOrderItemDTO();
        item.setProductId(UUID.randomUUID());
        item.setQuantity(2);
        item.setPrice(BigDecimal.valueOf(50));
        item.setSubTotal(BigDecimal.valueOf(100));

        return CreateOrderCommand.builder()
                .customerId(UUID.randomUUID())
                .restaurantId(UUID.randomUUID())
                .price(BigDecimal.valueOf(100))
                .items(List.of(item))
                .address(address)
                .build();
    }

    private TrackOrderQuery buildTrackOrderQuery() {
        return TrackOrderQuery.builder()
                .trackingId(UUID.randomUUID())
                .orderId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .build();
    }

    private CancelOrderCommand buildCancelOrderCommand() {
        return CancelOrderCommand.builder()
                .orderId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .build();
    }

    private TrackingOrderDTO buildTrackingDto(TrackOrderQuery query) {
        return TrackingOrderDTO.builder()
                .trackingId(query.getTrackingId())
                .orderId(query.getOrderId())
                .customerId(query.getCustomerId())
                .restaurantId(UUID.randomUUID())
                .orderStatus(OrderStatus.PENDING.name())
                .totalAmount(BigDecimal.valueOf(100))
                .items(List.of(buildOrderItemDto()))
                .build();
    }

    private TrackingOrderDTO buildTrackingDto(UUID orderId, UUID customerId) {
        return TrackingOrderDTO.builder()
                .trackingId(UUID.randomUUID())
                .orderId(orderId)
                .customerId(customerId)
                .restaurantId(UUID.randomUUID())
                .orderStatus(OrderStatus.CANCELLED.name())
                .totalAmount(BigDecimal.valueOf(100))
                .items(List.of(buildOrderItemDto()))
                .build();
    }

    private OrderItemDTO buildOrderItemDto() {
        return OrderItemDTO.builder()
                .productId(UUID.randomUUID())
                .price(BigDecimal.valueOf(50))
                .quantity(2)
                .subTotal(BigDecimal.valueOf(100))
                .build();
    }

    private Customer buildCustomer(UUID customerId) {
        return Customer.builder()
                .id(customerId.toString())
                .fullName("John Doe")
                .email("john.doe@example.com")
                .build();
    }

    private QueryCustomerResponse buildCustomerResponse(Customer customer) {
        QueryCustomerResponse response = new QueryCustomerResponse();
        response.setCustomer(customer);
        response.setMessage("found");
        response.setStatus("200");
        return response;
    }

    private Order buildOrder(UUID customerId, UUID restaurantId) {
        UUID orderUuid = UUID.randomUUID();
        OrderId orderId = OrderId.of(orderUuid);
        OrderItem orderItem = OrderItem.builder()
                .orderId(orderId)
                .productId(ProductId.of(UUID.randomUUID()))
                .price(new Money(BigDecimal.valueOf(50)))
                .quantity(2)
                .subTotal(new Money(BigDecimal.valueOf(100)))
                .build();

        Order order = Order.builder()
                .customerId(CustomerId.of(customerId))
                .restaurantId(RestaurantId.of(restaurantId))
                .deliveryAddress(new StreetAddress("123 Main", "10000", "Hanoi"))
                .price(new Money(BigDecimal.valueOf(100)))
                .items(List.of(orderItem))
                .trackingId(TrackingId.of(UUID.randomUUID()))
                .orderStatus(OrderStatus.PENDING)
                .failureMessages(null)
                .build();
        order.setId(orderId);
        return order;
    }
}
