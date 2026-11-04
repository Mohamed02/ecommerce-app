package com.ecommerce.invetory.service;

import ch.qos.logback.core.model.INamedModel;
import com.ecommerce.invetory.dto.AddStockRequest;
import com.ecommerce.invetory.dto.InventoryResponse;
import com.ecommerce.invetory.model.Inventory;
import com.ecommerce.invetory.model.InventoryReservation;
import com.ecommerce.invetory.model.ReservationStatus;
import com.ecommerce.invetory.repository.InventoryRepository;
import com.ecommerce.invetory.repository.InventoryReserverationRepository;
import com.ecommerce.order.dto.OrderItemRequest;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class InventoryService {

    private InventoryRepository inventoryRepository;
    private OrderRepository orderRepository;
    private InventoryReserverationRepository inventoryReserverationRepository;
    public  InventoryService(InventoryRepository inventoryRepository,OrderRepository orderRepository, InventoryReserverationRepository inventoryReserverationRepository){
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
        this.inventoryReserverationRepository= inventoryReserverationRepository;
    }
    @Transactional
    public InventoryResponse addStock(AddStockRequest addStockRequest){
        Inventory inventory = this.inventoryRepository.findByProductIdForUpdate(addStockRequest.getProductId())
                .orElseThrow(()->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST,"Product with id: "+ addStockRequest.getProductId()+" not found"));
        inventory.addStock(addStockRequest.getQuantity());
        return toInventoryResponse(inventory);
    }

    public InventoryResponse getInventoryDetails(Long productId){
        Inventory inventory= this.inventoryRepository.findByProductId(productId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.BAD_REQUEST,"Product with id: "+ productId+" not found"));
        return  toInventoryResponse(inventory);
    }


    @Transactional
    public void reserveStockForOrder(Long orderId){
         Order order = this.orderRepository.findByIdForUpdate(orderId)
                 .orElseThrow(()->   new ResponseStatusException(HttpStatus.BAD_REQUEST,"Order with id: "+ orderId+" not found"));
        Optional<List<InventoryReservation>> inventoryReservationList = inventoryReserverationRepository.findByOrderId(orderId);
        if (inventoryReservationList.isPresent() && !inventoryReservationList.get().isEmpty()){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Inventory reservation already exists for this order");
        }
        List<OrderItem> orderItems = order.getItems();
        List<Integer> quantities = new ArrayList<>();
        Map<Long,Integer> requestedQuantities = new TreeMap<>();

        for(OrderItem orderItem: orderItems){
            Long productId= orderItem.getProduct().getId();
            Integer quantity = orderItem.getQuantity();
            requestedQuantities.merge(productId,quantity,(oldQuantity,newQuantity)-> oldQuantity+ newQuantity);
        }

        List<Inventory> lockedInventories = new ArrayList<>();
        for(Map.Entry<Long, Integer> entry: requestedQuantities.entrySet()){
                Inventory inventory = this.inventoryRepository.findByProductIdForUpdate(entry.getKey()).
                        orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Prdouct with id: "+entry.getKey()+" not found"));

                if(inventory.getAvailableStock() < entry.getValue()){
                    throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"Insufficient stock for product");
                }
            lockedInventories.add(inventory);
                quantities.add(entry.getValue());
        }
        for(int i=0;i<lockedInventories.size();i++){
            Inventory lockedInventory = lockedInventories.get(i);
            Integer quantity =quantities.get(i);
            lockedInventory.reserveStock(quantity);
            InventoryReservation inventoryReservation = new InventoryReservation(order,lockedInventory.getProduct(),quantity);
            inventoryReserverationRepository.save(inventoryReservation);
        }
    }

    @Transactional
    public void confirmReserveration(Long orderId){
        updateReservationStatus(orderId,ReservationStatus.CONFIRMED);
    }

    @Transactional
    public void releaseReservation(Long orderId){
        updateReservationStatus(orderId,ReservationStatus.RELEASED);
    }


    private void updateReservationStatus(Long orderId,ReservationStatus targetStatus){

        // Retrive teh order
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Order found with Id: "+ orderId));

        // Retreive the InventoryReservations for the Order
        List<InventoryReservation> inventoryReservations  = inventoryReserverationRepository.findByOrderId(orderId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Inventory Reservation found for the Order: "+ orderId));
        if(inventoryReservations.stream().anyMatch((reservation)-> reservation.getStatus()!=ReservationStatus.RESERVED)){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Reservation is not in RESERVED status");
        }

        Map<Long,Integer> reservedQuantites = new TreeMap<>();
        List<Inventory> lockedinventories = new ArrayList<>();
        for(InventoryReservation reservation: inventoryReservations){
            reservation.setStatus(targetStatus);
            reservedQuantites.put(reservation.getProduct().getId(),reservation.getQuantity());
        }

        for(Map.Entry<Long,Integer> entry: reservedQuantites.entrySet()){
            Inventory inventory = inventoryRepository.findByProductIdForUpdate(entry.getKey())
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Inventory Reservation found for the Product: "+ entry.getKey()));
            lockedinventories.add(inventory);
        }

        for(Inventory inventory: lockedinventories){
            int reservedQuantity = reservedQuantites.get(inventory.getProduct().getId());
            if(targetStatus == ReservationStatus.CONFIRMED){
                inventory.confirmReservation(reservedQuantity);
            }else {
                inventory.releaseReservation(reservedQuantity);
            }

        }
        inventoryReserverationRepository.saveAll(inventoryReservations);
    }
    private InventoryResponse toInventoryResponse(Inventory inventory){
        return  new InventoryResponse(inventory.getProduct().getName(),inventory.getTotalStock(),inventory.getReservedStock(),inventory.getAvailableStock());
    }


}
