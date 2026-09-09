package com.cybertunnel.service;

import com.cybertunnel.model.MedPlan;
import com.cybertunnel.model.MedPlanItem;
import com.cybertunnel.repository.MedPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MedPlanService {

    private final MedPlanRepository repository;

    public MedPlanService(MedPlanRepository repository) {
        this.repository = repository;
    }

    /** 获取用户的所有用药方案 */
    @Transactional(readOnly = true)
    public List<MedPlan> findByUserId(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 新建用药方案
     * @param items 每项为 [药名, 片数] 的数组
     */
    @Transactional
    public Optional<MedPlan> create(Long userId, String planName, String note, List<String[]> items) {
        if (planName == null || planName.isBlank() || items == null || items.isEmpty()) {
            return Optional.empty();
        }
        MedPlan plan = new MedPlan();
        plan.setUserId(userId);
        plan.setPlanName(planName.trim());
        plan.setNote(note);
        for (String[] item : items) {
            String name = item[0].trim();
            Integer dosage;
            try { dosage = Integer.parseInt(item[1]); } catch (Exception e) { dosage = 1; }
            if (name.isEmpty()) continue;
            plan.addItem(new MedPlanItem(name, dosage));
        }
        if (plan.getItems().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(repository.save(plan));
    }

    /** 删除方案（归属校验：只允许删本人的） */
    @Transactional
    public void deleteById(Long userId, Long id) {
        if (repository.findByIdAndUserId(id, userId).isEmpty()) {
            return; // 不是本人的方案 -> 不删
        }
        repository.deleteById(id);
    }
}
