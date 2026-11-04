package com.ecommerce.invetory.dto;

import lombok.Data;

@Data
public class InventoryResponse {
    private String productName;
    private Integer totalStock;
    private Integer reservedStock;
    private Integer availableStock;

    public InventoryResponse(String productName, Integer totalStock, Integer reservedStock, Integer availableStock) {
        this.productName = productName;
        this.totalStock = totalStock;
        this.reservedStock = reservedStock;
        this.availableStock = availableStock;
    }
}
