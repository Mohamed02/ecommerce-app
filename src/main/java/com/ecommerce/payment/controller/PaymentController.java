package com.ecommerce.payment.controller;

import com.ecommerce.payment.Service.PaymentService;
import com.ecommerce.payment.dto.InitiatePaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.model.PaymentMethod;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private PaymentService paymentService;
    public  PaymentController(PaymentService paymentService){
        this.paymentService= paymentService;
    }
    @PostMapping("/order/{orderId}")
    public PaymentResponse intitiatePayment(@PathVariable Long orderId, @Valid  @RequestBody InitiatePaymentRequest initiatePaymentRequest){
        return  paymentService.intiatePayment(orderId, initiatePaymentRequest);
    }

    @PostMapping("{paymentId}/simulate-success")
    public PaymentResponse simulateSuccess(@PathVariable Long paymentId){
        return  paymentService.simulatePaymentSuccess(paymentId);
    }

    @PostMapping("{paymentId}/simulate-failure")
    public PaymentResponse simulateFailure(@PathVariable Long paymentId){
        return  paymentService.simulatePaymentFailure(paymentId);
    }
    @GetMapping("/{paymentId}")
    public PaymentResponse getPaymentById(
            @PathVariable Long paymentId
    ) {
        return paymentService.getPaymentById(paymentId);
    }

    @GetMapping("/orders/{orderId}")
    public PagedModel<PaymentResponse> getPaymentsByOrderId(
            @PathVariable Long orderId,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return new PagedModel<>(paymentService.getPaymentByOrderId(
                orderId,
                pageable
        ));
    }



}
