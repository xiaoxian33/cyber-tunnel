package com.cybertunnel.repository;

import com.cybertunnel.model.MedicineInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 药品库存 —— 数据访问层
 */
@Repository
public interface MedicineInventoryRepository extends JpaRepository<MedicineInventory, Long> {

    /** 按用户 + 药品名查找库存（归属校验用） */
    Optional<MedicineInventory> findByUserIdAndMedicineName(Long userId, String medicineName);

    /** 按用户查找所有库存 */
    List<MedicineInventory> findByUserId(Long userId);
}
