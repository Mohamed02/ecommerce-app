package com.ecommerce.invetory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddStockRequest {

    @NotNull
    private Long productId;

    @NotNull
    @Min(0)
    private Integer quantity;

    public AddStockRequest() {
    }

    public AddStockRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
