package com.ecommerce.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ProductResponse {
    private  Long id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private Integer quantity = 0;
    private Boolean active;
    public ProductResponse(
            Long id,
            String name,
            String description,
            String sku,
            BigDecimal price,
            Integer quantity,
            Boolean active) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.price = price;
        this.quantity = quantity;
        this.active = active;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getSku() { return sku; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public Boolean getActive() { return active; }
}
