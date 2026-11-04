package com.ecommerce.order.service;

import com.ecommerce.customer.model.Customer;
import com.ecommerce.customer.repository.CustomerRepository;
import com.ecommerce.invetory.service.InventoryService;
import com.ecommerce.order.dto.OrderItemRequest;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    /** TODO
     * 1. Unit tests for OrderService
     * Add tests for:
     * - getOrderById() returns the mapped order when it exists.
     * - getOrderById() throws 404 NOT_FOUND when the order does not exist.
     * - getCustomerOrders() returns mapped results from the repository.
     * - Passing null status calls findByCustomerId().
     * - Passing CONFIRMED status calls findByCustomerIdAndStatus().
     *
     * verify(orderRepository).findByCustomerIdAndStatus(
     *         eq(customerId),
     *         eq(OrderStatus.CONFIRMED),
     *         eq(pageable)
     * );
     * */

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrderSuccessfully() throws Exception{

        // Arrange
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(1L);

        Product product = createProduct(
                1L, "Laptop", "LAP-001",
                "50000.00", 10, true);


        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderRequest request = createOrderRequest(1L, 1L, 2);

        // Act
        OrderResponse response = null;
            response = orderService.createOrder(request);


        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getCustomerId());
        assertEquals(0,
                new BigDecimal("100000.00")
                        .compareTo(response.getTotalAmount()));

        assertEquals(1, response.getItems().size());
        assertEquals(2, response.getItems().get(0).getQuantity());


        verify(inventoryService)
                .reserveStockForOrder(anyLong());
        verify(orderRepository)
                .save(any(Order.class));
        // Verify order was saved.
        verify(orderRepository, times(1)).save(any(Order.class));
    }
    @Test
    void shouldThrowExceptionWhenStockIsInsufficient() {

        Customer customer = mock(Customer.class);
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setSku("LAP-001");
        product.setPrice(new BigDecimal("50000.00"));
        product.setQuantity(2);
        product.setActive(true);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        OrderRequest request = createOrderRequest(1L, 1L, 5);

        var exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.createOrder(request)
        );

        assertEquals(
                400,
                exception.getStatusCode().value()
        );

        verify(orderRepository, never()).save(any(Order.class));
    }
    @Test
    void shouldPropagateInventoryReservationFailure() {

        Customer customer = mock(Customer.class);

        Product product = createProduct(
                1L,
                "Laptop",
                "LAP-001",
                "50000.00",
                10,
                true
        );

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    // Simulate an assigned database ID.
                    return order;
                });

        doThrow(new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Insufficient stock"))
                .when(inventoryService)
                .reserveStockForOrder(any());

        OrderRequest request = createOrderRequest(1L, 1L, 2);

        assertThrows(
                ResponseStatusException.class,
                () -> orderService.createOrder(request)
        );
    }

    private Product createProduct(
            Long id,
            String name,
            String sku,
            String price,
            int quantity,
            boolean active) {

        Product product = mock(Product.class);

        when(product.getId()).thenReturn(id);
        when(product.getName()).thenReturn(name);
        when(product.getSku()).thenReturn(sku);
        when(product.getPrice()).thenReturn(new BigDecimal(price));
        when(product.getQuantity()).thenReturn(quantity);
        when(product.getActive()).thenReturn(active);

        return product;
    }

    private OrderRequest createOrderRequest(
            Long customerId,
            Long productId,
            int quantity) {

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(productId);
        item.setQuantity(quantity);

        OrderRequest request = new OrderRequest();
        request.setCustomerId(customerId);
        request.setItems(List.of(item));

        return request;
    }


}