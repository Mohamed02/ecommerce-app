package com.ecommerce.invetory.model;

import com.ecommerce.order.model.Order;
import com.ecommerce.product.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inventory_reservations")
public class InventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Min(1)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReservationStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public InventoryReservation() {
    }

    public InventoryReservation(Order order, Product product, Integer quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.status=ReservationStatus.RESERVED;
        this.createdAt = LocalDateTime.now();
    }
}
