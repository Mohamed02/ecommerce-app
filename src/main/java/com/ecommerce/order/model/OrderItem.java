package com.ecommerce.order.model;

import com.ecommerce.product.model.Product;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;

    @Column(nullable = false, length = 50)
    private String sku;

    @Column(nullable = false)
    private Integer quantity;


    @Column(name = "unit_price", nullable = false,
            precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "line_total", nullable = false,
            precision = 12, scale = 2)
    private BigDecimal lineTotal;

    public OrderItem() {
    }

    public OrderItem(Product product,
                     Integer quantity,
                     BigDecimal unitPrice,
                     BigDecimal lineTotal) {
        this.product = product;
        this.productName = product.getName();
        this.sku = product.getSku();
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }


    public void setOrder(Order order) {
        this.order = order;
    }

}
