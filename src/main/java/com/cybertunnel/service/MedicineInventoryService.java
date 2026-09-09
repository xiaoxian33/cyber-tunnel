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

    /** 按用户 + 药品名获取库存（归属校验用） */
    @Transactional(readOnly = true)
    public Optional<MedicineInventory> findByUserIdAndMedicineName(Long userId, String medicineName) {
        return repository.findByUserIdAndMedicineName(userId, medicineName);
    }

    /** 设置/更新库存（没有就新建，有就覆盖；始终限定在当前用户下） */
    @Transactional
    public MedicineInventory setStock(Long userId, String medicineName, Integer stock) {
        Optional<MedicineInventory> existing = repository.findByUserIdAndMedicineName(userId, medicineName);
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

    /** 服用扣减（减库存；仅限本人） */
    @Transactional
    public Optional<MedicineInventory> take(Long userId, String medicineName, Integer count) {
        return repository.findByUserIdAndMedicineName(userId, medicineName).map(inv -> {
            int newStock = Math.max(0, inv.getStock() - count);
            inv.setStock(newStock);
            return repository.save(inv);
        });
    }

    /** 补货增加（加库存；仅限本人） */
    @Transactional
    public Optional<MedicineInventory> add(Long userId, String medicineName, Integer count) {
        return repository.findByUserIdAndMedicineName(userId, medicineName).map(inv -> {
            inv.setStock(inv.getStock() + count);
            return repository.save(inv);
        });
    }
}
