package com.ecommerce.invetory.controller;

import com.ecommerce.invetory.dto.AddStockRequest;
import com.ecommerce.invetory.dto.InventoryResponse;
import com.ecommerce.invetory.repository.InventoryRepository;
import com.ecommerce.invetory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private InventoryService inventoryService;

    public  InventoryController(InventoryService inventoryService){
        this.inventoryService =inventoryService;
    }
    @PostMapping("/stock")
    public ResponseEntity<InventoryResponse> addStock(@Valid @RequestBody(required = true) AddStockRequest addStockRequest){
        return new ResponseEntity<>(this.inventoryService.addStock(addStockRequest), HttpStatus.OK) ;
    }
    @GetMapping("/products/{productId}")
    public InventoryResponse getInventory(@PathVariable Long productId){
        return  inventoryService.getInventoryDetails(productId);
    }

    @PostMapping("/orders/{orderId}/reserve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reserveStock(@PathVariable Long orderId){
        inventoryService.reserveStockForOrder(orderId);
    }

    @PostMapping("/orders/{orderId}/release")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void releaseReservation(@PathVariable Long orderId){
        inventoryService.releaseReservation(orderId);
    }

    @PostMapping("/orders/{orderId}/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirmReservation(@PathVariable Long orderId){
        inventoryService.confirmReserveration(orderId);
    }


}
