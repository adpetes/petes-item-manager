package com.petesitemmanager.pim.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petesitemmanager.pim.domain.InventoryItem;

// public interface InventoryItemRepository extends JpaRepository<InventoryItem,
// Long> {
// Optional<InventoryItem> findByHashVal(Long hashVal);

// // @Query("SELECT i FROM InventoryItem i WHERE i.hashVal IN :hashVals")
// // List<InventoryItem> findAllWithHashValsIn(List<Long> hashVals);

// List<InventoryItem> findAllByHashValIn(List<Long> itemHashList);
// }
