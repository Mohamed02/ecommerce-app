package com.ecommerce.order.service;

import com.ecommerce.customer.model.Customer;
import com.ecommerce.customer.repository.CustomerRepository;
import com.ecommerce.order.dto.OrderItemRequest;
import com.ecommerce.order.dto.OrderItemResponse;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    OrderRepository orderRepository;
    ProductRepository productRepository;
    CustomerRepository customerRepository;
    OrderService(OrderRepository orderRepository, ProductRepository productRepository, CustomerRepository customerRepository){
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;

    }
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) throws Exception{
          Customer customer = customerRepository.findById(orderRequest.getCustomerId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer with ID: "+orderRequest.getCustomerId()+" doesnt exists"));
        Order order = new Order(customer, LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.ZERO);
        BigDecimal totalAmount = BigDecimal.ZERO;

        for(OrderItemRequest orderitemRequest: orderRequest.getItems()){

            Product product  = this.productRepository.findById(orderitemRequest.getProductId())
                    .orElseThrow( ()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Product Does Found"+orderitemRequest.getProductId()));
            if(!Boolean.TRUE.equals(product.getActive())){
               throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"Product Is Inactive for ID"+product.getId());
            }
            int requestedQuantity = orderitemRequest.getQuantity();
            if(product.getQuantity() < requestedQuantity){
               throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"Insufficent Stock for the Product "+product.getId());
            }

            BigDecimal unitPrice = product.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(orderitemRequest.getQuantity()));
            product.setQuantity(product.getQuantity() - orderitemRequest.getQuantity());
            OrderItem item = new OrderItem(product,orderitemRequest.getQuantity(),product.getPrice(),lineTotal);
            order.addItem(item);
            totalAmount =lineTotal.add(totalAmount);
        }
        order.setTotalAmount(totalAmount);
        Order savedOrder = this.orderRepository.save(order);
        return  this.toResponse(savedOrder);
    }

    public Page<OrderResponse> getAllOrder(Pageable pageable){
        Page<OrderResponse> order = this.orderRepository.findAll(pageable).map(this::toResponse);
        return order;
    }

    public OrderResponse getOrderById(Long orderId){
     Order order = this.orderRepository.findById(orderId).orElseThrow( ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with ID:"+ orderId+" Doesnt exists" ));
     return  toResponse(order);
    }

    public Page<OrderResponse> getOrdersByCustomerId(Long customerId, Pageable pageable){
        return  this.orderRepository.findByCustomerId(customerId, pageable).map(this::toResponse);
    }
    private OrderResponse toResponse(Order savedOrder) {
        List<OrderItemResponse> orderItemsResponse = savedOrder.getItems().stream().map((item)
                -> new OrderItemResponse(item.getProduct().getId(),item.getProductName(), item.getSku(), item.getQuantity(),item.getUnitPrice(),item.getUnitPrice())).toList();
        return  new OrderResponse(savedOrder.getId(),savedOrder.getCustomer().getId(),savedOrder.getOrderDate(),savedOrder.getStatus(),savedOrder.getTotalAmount(),orderItemsResponse);
    }


}
