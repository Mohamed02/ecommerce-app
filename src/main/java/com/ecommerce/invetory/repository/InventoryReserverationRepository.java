package com.ecommerce.invetory.repository;

import com.ecommerce.invetory.model.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryReserverationRepository extends JpaRepository<InventoryReservation, Long> {

    Optional<List<InventoryReservation>> findByOrderId(Long orderId);

}
