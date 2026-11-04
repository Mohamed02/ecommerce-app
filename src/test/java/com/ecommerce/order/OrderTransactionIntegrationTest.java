package com.ecommerce.order;

import com.ecommerce.customer.model.Customer;
import com.ecommerce.customer.repository.CustomerRepository;
import com.ecommerce.invetory.model.Inventory;
import com.ecommerce.invetory.repository.InventoryRepository;
import com.ecommerce.order.dto.OrderItemRequest;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderService;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class OrderTransactionIntegrationTest {

    /*
    * TODO
    *  2. Integration tests
    With your H2 test profile, create a customer and a few orders with different statuses. Then verify:
    - The customer’s order list is paginated.
    - Filtering by CONFIRMED returns only confirmed orders.
    - The response includes pagination metadata such as totalElements and totalPages.
    - Requesting a nonexistent order ID returns 404
    *
    * */

    @Autowired
    private  CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;
    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    void shouldRollbackEntireOrderWhenOneProductHasInsufficientStock(){
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("rollback-test@example.com");
        customer.setPhone("9876543210");
        customer = customerRepository.save(customer);

        Product laptop = new Product(
                "Test Laptop",
                "Test Laptop Description",
                "ROLLBACK-LAPTOP",
                BigDecimal.valueOf(50000.00),
                10
        );
        productRepository.save(laptop);

        Product mobile = new Product(
                "Test Mobile",
                "Test Mobile Description",
                "ROLLBACK-MOBILE",
                BigDecimal.valueOf(20000.00),
                1
        );
        productRepository.save(mobile);

        long orderCountBefore = orderRepository.count();

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(customer.getId());

        OrderItemRequest orderItemRequest1 = new OrderItemRequest();
        orderItemRequest1.setProductId(laptop.getId());
        orderItemRequest1.setQuantity(5);

        OrderItemRequest orderItemRequest2 = new OrderItemRequest();
        orderItemRequest1.setProductId(mobile.getId());
        orderItemRequest1.setQuantity(5);

        Inventory laptopInventory = new Inventory(laptop, 10);
        inventoryRepository.save(laptopInventory);

        Inventory mobileInventory = new Inventory(mobile, 1);
        inventoryRepository.save(mobileInventory);

        Inventory updatedLaptopInventory =
                inventoryRepository.findByProductId(laptop.getId())
                        .orElseThrow();

        Inventory updatedMobileInventory =
                inventoryRepository.findByProductId(mobile.getId())
                        .orElseThrow();

        List<OrderItemRequest> orderItems = new ArrayList<OrderItemRequest>();
        orderItems.add(orderItemRequest1);
        orderItems.add(orderItemRequest2);

        orderRequest.setItems(orderItems);

       assertThrows(ResponseStatusException.class,()-> orderService.createOrder(orderRequest));
        assertEquals(orderCountBefore,orderRepository.count());

        assertEquals(10, updatedLaptopInventory.getTotalStock());
        assertEquals(0, updatedLaptopInventory.getReservedStock());

        assertEquals(1, updatedMobileInventory.getTotalStock());
        assertEquals(0, updatedMobileInventory.getReservedStock());

    }
}
