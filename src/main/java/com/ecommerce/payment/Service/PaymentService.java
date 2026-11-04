package com.ecommerce.payment.Service;

import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.service.OrderService;
import com.ecommerce.payment.dto.InitiatePaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.model.Payment;
import com.ecommerce.payment.model.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.hibernate.internal.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PaymentService {

    private OrderService orderService;
    private PaymentRepository paymentRepository;
    public  PaymentService(OrderService orderService, PaymentRepository paymentRepository){
    this.orderService = orderService;
    this.paymentRepository = paymentRepository;

    }
    @Transactional
    public PaymentResponse intiatePayment(Long orderId, InitiatePaymentRequest paymentRequest){

       OrderResponse order = orderService.getPendingOrderForPayment(orderId);

        if(paymentRepository.existsByOrderIdAndStatus(orderId,PaymentStatus.INITIATED)){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An initiated payment already exists for this order"
            );
        }
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(paymentRequest.paymentMethod());
        payment.setStatus(PaymentStatus.INITIATED);

        Payment savedPayment = paymentRepository.save(payment);

        return toResponse(savedPayment);
    };

    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment not found"
                ));

        return toResponse(payment);
    };

    public Page<PaymentResponse> getPaymentByOrderId(Long orderId, Pageable pageable){
        orderService.getOrderById(orderId);
        return  paymentRepository.findByOrderId(orderId, pageable).map(this::toResponse);
    }

    @Transactional
    public PaymentResponse simulatePaymentSuccess(Long paymentId){
       Payment payment = paymentRepository.findByIdForUpdate(paymentId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Payment with id: "+ paymentId+" not found"));
        if (payment.getStatus() != PaymentStatus.INITIATED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only initiated payments can be failed"
            );
        }
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionReference("SIM-"+payment.getId());
        orderService.confirmOrder(payment.getOrderId());
       return  toResponse(payment);
    };

    @Transactional
    public PaymentResponse simulatePaymentFailure(Long paymentId){
        Payment payment = paymentRepository.findByIdForUpdate(paymentId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Payment with id: "+ paymentId+" not found"));
        if (payment.getStatus() != PaymentStatus.INITIATED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only initiated payments can be succeded"
            );
        }
        payment.setStatus(PaymentStatus.FAILED);
        orderService.cancelOrder(payment.getOrderId());
        return  toResponse(payment);
    };

    public  PaymentResponse toResponse(Payment payment){
      return   new PaymentResponse(payment.getId(),payment.getOrderId(),payment.getAmount(),payment.getStatus(),payment.getPaymentMethod(),payment.getTransactionReference(),payment.getCreatedAt());
    };
}
