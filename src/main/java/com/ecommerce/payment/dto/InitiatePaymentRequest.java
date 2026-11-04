package com.ecommerce.payment.dto;

import com.ecommerce.payment.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record InitiatePaymentRequest(@NotNull PaymentMethod paymentMethod) {

}
