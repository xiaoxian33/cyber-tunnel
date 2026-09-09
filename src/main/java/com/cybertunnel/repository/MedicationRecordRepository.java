package com.cybertunnel.repository;

import com.cybertunnel.model.MedicationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 药品服用记录 —— 数据访问层
 */
@Repository
public interface MedicationRecordRepository extends JpaRepository<MedicationRecord, Long> {

    /** 按用户查询所有记录，按服药时间倒序 */
    List<MedicationRecord> findByUserIdOrderByTakenAtDesc(Long userId);

    /** 按用户查询所有记录（顺序无关，供统计用） */
    List<MedicationRecord> findByUserId(Long userId);

    /** 查询某用户的一条记录（归属校验用：防止改/删别人的数据） */
    Optional<MedicationRecord> findByIdAndUserId(Long id, Long userId);

    /** 查询公开记录，按时间倒序 */
    List<MedicationRecord> findByPrivacyOrderByTakenAtDesc(MedicationRecord.Privacy privacy);

    /** 查询用户未归档的记录 */
    List<MedicationRecord> findByUserIdAndArchivedFalse(Long userId);

    /** 查询用户已归档的记录 */
    List<MedicationRecord> findByUserIdAndArchivedTrue(Long userId);
}

