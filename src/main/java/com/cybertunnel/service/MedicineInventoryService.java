package com.cybertunnel.service;

import com.cybertunnel.model.MedicineInventory;
import com.cybertunnel.repository.MedicineInventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MedicineInventoryService {

    private final MedicineInventoryRepository repository;

    public MedicineInventoryService(MedicineInventoryRepository repository) {
        this.repository = repository;
    }

    /** 获取用户所有库存 */
    @Transactional(readOnly = true)
    public List<MedicineInventory> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    /** 按药品名获取库存 */
    @Transactional(readOnly = true)
    public Optional<MedicineInventory> findByMedicineName(String medicineName) {
        return repository.findByMedicineName(medicineName);
    }

    /** 设置/更新库存（没有就新建，有就覆盖） */
    @Transactional
    public MedicineInventory setStock(Long userId, String medicineName, Integer stock) {
        Optional<MedicineInventory> existing = repository.findByMedicineName(medicineName);
        if (existing.isPresent()) {
            MedicineInventory inv = existing.get();
            inv.setStock(stock);
            return repository.save(inv);
        } else {
            MedicineInventory inv = new MedicineInventory(medicineName, stock);
            inv.setUserId(userId);
            return repository.save(inv);
        }
    }

    /** 服用扣减（减库存） */
    @Transactional
    public Optional<MedicineInventory> take(String medicineName, Integer count) {
        return repository.findByMedicineName(medicineName).map(inv -> {
            int newStock = Math.max(0, inv.getStock() - count);
            inv.setStock(newStock);
            return repository.save(inv);
        });
    }

    /** 补货增加（加库存） */
    @Transactional
    public Optional<MedicineInventory> add(String medicineName, Integer count) {
        return repository.findByMedicineName(medicineName).map(inv -> {
            inv.setStock(inv.getStock() + count);
            return repository.save(inv);
        });
    }
}
