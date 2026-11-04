package com.ecommerce.payment.dto;

import com.ecommerce.payment.model.PaymentMethod;
import com.ecommerce.payment.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(Long id, Long orderId, BigDecimal amount, PaymentStatus status, PaymentMethod paymentMethod, String transactionReference, LocalDateTime createdAt) {

}
