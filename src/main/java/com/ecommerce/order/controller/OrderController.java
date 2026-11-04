package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private  OrderService orderService;
    OrderController(OrderService orderService){
        this.orderService = orderService;
    }
    @GetMapping("/orders")
    public ResponseEntity<PagedModel<OrderResponse>> getAllOrders(@PageableDefault(page = 0, size = 10, sort = "id",direction = Sort.Direction.ASC) Pageable pageable){
        return new ResponseEntity<>(new PagedModel<>(this.orderService.getAllOrder(pageable)),HttpStatus.OK);
    }
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId){
        return  new ResponseEntity<>(this.orderService.getOrderById(orderId),HttpStatus.OK);
    }
    @GetMapping("/orders/customer/{customerId}")
    public ResponseEntity<PagedModel<OrderResponse>> getCustomerOrders(@PathVariable Long customerId, @RequestParam(required = false) OrderStatus status, @PageableDefault( page = 0,
            size = 10,
            sort = "orderDate",
            direction = Sort.Direction.DESC ) Pageable pageable){
        return  new ResponseEntity<>(new PagedModel<>(this.orderService.getCustomerOrders(customerId, status , pageable)),HttpStatus.FOUND);
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) throws Exception{
        return  new ResponseEntity<>(this.orderService.createOrder(orderRequest), HttpStatus.CREATED);
    }

    @PostMapping("/orders/{orderId}/confirm")
    public void confirmOrder(@PathVariable Long orderId){
        orderService.confirmOrder(orderId);
    }

    @PostMapping("/orders/{orderId}/cancel")
    public void cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
    }

}
