package com.cybertunnel.model;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * 药品服用记录表 —— 每次吃药生成一条记录
 */
@Entity
@Table(name = "medication_record")
public class MedicationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户ID */
    @Column(name = "user_id", nullable = false)
    private Long userId = 1L;

    /** 药品名称 */
    @Column(name = "medicine_name", nullable = false)
    private String medicineName;

    /** 服用片数 */
    @Column(nullable = false)
    private Integer dosage;

    /** 服药时间 */
    @Column(name = "taken_at", nullable = false)
    private LocalDateTime takenAt;

    /** 是否常用药 */
    @Column(name = "is_common", nullable = false)
    private Boolean isCommon = false;

    /** 本次是否为正常用药；false 则视为超量风险剂量 */
    @Column(name = "is_normal_dose", nullable = false)
    private Boolean isNormalDose = true;

    /** 公开 or 私密 */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Privacy privacy = Privacy.PRIVATE;

    /** 心得描述 */
    @Column(columnDefinition = "TEXT")
    private String thoughts;

    /** 是否已归档（封存） */
    @Column(nullable = false)
    private Boolean archived = false;

    /** 归档时的历时（小时） */
    @Column(name = "duration_hours")
    private Double durationHours;

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public MedicationRecord() {}

    // ====== Getter / Setter ======

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public Integer getDosage() { return dosage; }
    public void setDosage(Integer dosage) { this.dosage = dosage; }
    public LocalDateTime getTakenAt() { return takenAt; }
    public void setTakenAt(LocalDateTime takenAt) { this.takenAt = takenAt; }
    public Boolean getIsCommon() { return isCommon; }
    public void setIsCommon(Boolean isCommon) { this.isCommon = isCommon; }
    public Boolean getIsNormalDose() { return isNormalDose; }
    public void setIsNormalDose(Boolean isNormalDose) { this.isNormalDose = isNormalDose != null ? isNormalDose : true; }
    public Privacy getPrivacy() { return privacy; }
    public void setPrivacy(Privacy privacy) { this.privacy = privacy; }
    public String getThoughts() { return thoughts; }
    public void setThoughts(String thoughts) { this.thoughts = thoughts; }
    public Boolean getArchived() { return archived; }
    public void setArchived(Boolean archived) { this.archived = archived; }
    public Double getDurationHours() { return durationHours; }
    public void setDurationHours(Double durationHours) { this.durationHours = durationHours; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    /** 公开/私密 枚举 */
    public enum Privacy {
        PRIVATE,
        PUBLIC
    }
}

