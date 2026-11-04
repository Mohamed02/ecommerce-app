package com.ecommerce.invetory.model;


import com.ecommerce.invetory.dto.AddStockRequest;
import com.ecommerce.invetory.dto.InventoryResponse;
import com.ecommerce.product.model.Product;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name="total_stock", nullable = false)
    private Integer totalStock;

    @Column(name="reserved_stock", nullable = false)
    private Integer reservedStock;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public  Inventory(){

    }
    public Inventory(Product product, Integer totalStock) {
        this.product = product;
        this.totalStock = totalStock;
    }

    public void addStock(Integer quantity){
        this.setTotalStock(totalStock+quantity);
    }
    public Integer getAvailableStock(){
        return totalStock-reservedStock;
    }
    public void reserveStock(Integer unitsToBeReserved){

        if(unitsToBeReserved <=0 ){
            throw  new IllegalArgumentException("Reservation quantity must be postive");
        }else if(unitsToBeReserved > this.totalStock){
            throw  new IllegalArgumentException("Insufficient available stock");
        }
        this.reservedStock += unitsToBeReserved;
        this.updatedAt=  LocalDateTime.now();
    }
    public void confirmReservation(int quantity) {
        if (quantity <= 0 || reservedStock < quantity) {
            throw new IllegalArgumentException(
                    "Invalid reservation confirmation");
        }

        totalStock -= quantity;
        reservedStock -= quantity;
        updatedAt = LocalDateTime.now();
    }
    public void releaseReservation(int quantity) {
        if (quantity <= 0 || reservedStock < quantity) {
            throw new IllegalArgumentException(
                    "Invalid reservation release");
        }

        reservedStock -= quantity;
        updatedAt = LocalDateTime.now();
    }
}
