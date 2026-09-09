package com.cybertunnel.repository;

import com.cybertunnel.model.MedPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedPlanRepository extends JpaRepository<MedPlan, Long> {

    /** 按用户查询所有方案 */
    List<MedPlan> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** 查询某用户的一个方案（归属校验用：防止删别人的） */
    Optional<MedPlan> findByIdAndUserId(Long id, Long userId);
}
