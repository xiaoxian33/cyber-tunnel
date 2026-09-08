package com.cybertunnel.repository;

import com.cybertunnel.model.MedPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedPlanRepository extends JpaRepository<MedPlan, Long> {

    /** 按用户查询所有方案 */
    List<MedPlan> findByUserIdOrderByCreatedAtDesc(Long userId);
}
