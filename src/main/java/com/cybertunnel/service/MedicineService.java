package com.cybertunnel.service;

import com.cybertunnel.model.Medicine;
import com.cybertunnel.repository.MedicineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    /** 查询某个用户的所有药品 */
    @Transactional(readOnly = true)
    public List<Medicine> findByUserId(Long userId) {
        return medicineRepository.findByUserId(userId);
    }

    /** 按 ID 查询 */
    @Transactional(readOnly = true)
    public Optional<Medicine> findById(Long id) {
        return medicineRepository.findById(id);
    }

    /** 按名称 + 用户ID 查询 */
    @Transactional(readOnly = true)
    public Optional<Medicine> findByUserIdAndName(Long userId, String name) {
        return medicineRepository.findByUserIdAndName(userId, name);
        
    }

    /**
     * 新增药品
     * 如果同用户下同名药已存在，返回 empty
     */
    @Transactional
    public Optional<Medicine> createMedicine(Long userId, String name, Integer stock, Boolean isCommon) {
        if (medicineRepository.findByUserIdAndName(userId, name).isPresent()) {
            return Optional.empty();
        }
        Medicine medicine = new Medicine(userId, name, stock, isCommon);
        Medicine saved = medicineRepository.save(medicine);
        return Optional.of(saved);
    }

    /**
     * 调整库存
     */
    @Transactional
    public Optional<Medicine> adjustStock(Long id, int delta) {
        return medicineRepository.findById(id).map(medicine -> {
            int newStock = Math.max(0, medicine.getStock() + delta);
            medicine.setStock(newStock);
            return medicineRepository.save(medicine);
        });
    }

    /** 删除药品 */
    @Transactional
    public void deleteById(Long id) {
        medicineRepository.deleteById(id);
    }
}

