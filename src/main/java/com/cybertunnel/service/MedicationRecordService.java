package com.cybertunnel.service;

import com.cybertunnel.model.MedicationRecord;
import com.cybertunnel.repository.MedicationRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MedicationRecordService {

    private final MedicationRecordRepository repository;
    private final com.cybertunnel.repository.PostCommentRepository commentRepository;
    private final com.cybertunnel.repository.PostLikeRepository likeRepository;

    public MedicationRecordService(MedicationRecordRepository repository,
                                   com.cybertunnel.repository.PostCommentRepository commentRepository,
                                   com.cybertunnel.repository.PostLikeRepository likeRepository) {
        this.repository = repository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
    }

    /** 获取用户的所有记录 */
    @Transactional(readOnly = true)
    public List<MedicationRecord> findByUserId(Long userId) {
        return repository.findByUserIdOrderByTakenAtDesc(userId);
    }

    /** 获取所有公开记录 */
    @Transactional(readOnly = true)
    public List<MedicationRecord> findPublic() {
        return repository.findByPrivacyOrderByTakenAtDesc(MedicationRecord.Privacy.PUBLIC);
    }

    /** 新增一条服药记录 */
    @Transactional
    public MedicationRecord create(Long userId, String medicineName, Integer dosage,
                                    LocalDateTime takenAt, Boolean isCommon,
                                    Boolean isNormalDose,
                                    MedicationRecord.Privacy privacy) {
        MedicationRecord record = new MedicationRecord();
        record.setUserId(userId);
        record.setMedicineName(medicineName);
        record.setDosage(dosage);
        record.setTakenAt(takenAt);
        record.setIsCommon(isCommon != null ? isCommon : false);
        record.setIsNormalDose(isNormalDose != null ? isNormalDose : true);
        record.setPrivacy(privacy != null ? privacy : MedicationRecord.Privacy.PRIVATE);
        record.setArchived(false);
        return repository.save(record);
    }

    /** 更新心得描述 */
    @Transactional
    public Optional<MedicationRecord> updateThoughts(Long id, String thoughts) {
        return repository.findById(id).map(record -> {
            record.setThoughts(thoughts);
            return repository.save(record);
        });
    }

    /** 归档（封存）用户所有未归档记录 */
    @Transactional
    public int archiveAll(Long userId) {
        List<MedicationRecord> records = repository.findByUserIdAndArchivedFalse(userId);
        for (MedicationRecord record : records) {
            record.setArchived(true);
            // 计算历时（从服药时间到现在的时长）
            long hours = ChronoUnit.HOURS.between(record.getTakenAt(), LocalDateTime.now());
            record.setDurationHours((double) hours);
        }
        repository.saveAll(records);
        return records.size();
    }

    /** 删除记录（同时清理其评论与点赞） */
    @Transactional
    public void deleteById(Long id) {
        commentRepository.deleteByRecordId(id);
        likeRepository.deleteByRecordId(id);
        repository.deleteById(id);
    }

    // ===== 剂量风险统计（isNormalDose=false 记为一次超量风险） =====

    private long countRiskSince(Long userId, LocalDate start) {
        LocalDateTime since = start.atStartOfDay();
        return repository.findByUserId(userId).stream()
                .filter(r -> Boolean.FALSE.equals(r.getIsNormalDose()))
                .filter(r -> r.getTakenAt() != null && !r.getTakenAt().isBefore(since))
                .count();
    }

    private long countRiskBetween(Long userId, LocalDate start, LocalDate end) {
        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to = end.plusDays(1).atStartOfDay();
        return repository.findByUserId(userId).stream()
                .filter(r -> Boolean.FALSE.equals(r.getIsNormalDose()))
                .filter(r -> r.getTakenAt() != null && !r.getTakenAt().isBefore(from) && r.getTakenAt().isBefore(to))
                .count();
    }

    /**
     * 汇总剂量风险统计：7/14/30 天次数 + 本周 vs 上周对比 + 连续记录天数
     */
    @Transactional(readOnly = true)
    public Map<String, Object> riskStats(Long userId) {
        LocalDate today = LocalDate.now();

        long od7 = countRiskSince(userId, today.minusDays(6));
        long od14 = countRiskSince(userId, today.minusDays(13));
        long od30 = countRiskSince(userId, today.minusDays(29));

        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        long weekCurrent = countRiskSince(userId, weekStart);
        long weekPrevious = countRiskBetween(userId, weekStart.minusWeeks(1), weekStart.minusDays(1));

        String trend;
        if (weekCurrent < weekPrevious) trend = "进步了 👏";
        else if (weekCurrent > weekPrevious) trend = "退步了 📉";
        else trend = "持平 →";

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("riskDays7", od7);
        result.put("riskDays14", od14);
        result.put("riskDays30", od30);
        result.put("weekCurrent", weekCurrent);
        result.put("weekPrevious", weekPrevious);
        result.put("trend", trend);
        result.put("consecutiveDays", consecutiveRecordDays(userId));
        return result;
    }

    /** 计算用户连续有记录的天数 */
    private int consecutiveRecordDays(Long userId) {
        List<MedicationRecord> records = repository.findByUserId(userId);
        if (records.isEmpty()) return 0;
        java.util.Set<LocalDate> dates = new java.util.HashSet<>();
        for (MedicationRecord r : records) {
            if (r.getTakenAt() != null) dates.add(r.getTakenAt().toLocalDate());
        }
        int count = 0;
        LocalDate day = LocalDate.now();
        while (dates.contains(day)) {
            count++;
            day = day.minusDays(1);
        }
        return count;
    }
}

