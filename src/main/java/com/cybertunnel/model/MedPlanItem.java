package com.cybertunnel.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 用药方案明细 —— 一个方案里的某一种药，带对应片数
 */
@Entity
@Table(name = "med_plan_item")
public class MedPlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属方案（序列化时忽略其 items 避免无限递归） */
    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonIgnoreProperties({"items", "userId", "note", "createdAt"})
    private MedPlan plan;

    /** 药品名称 */
    @Column(name = "medicine_name", nullable = false)
    private String medicineName;

    /** 该药片数 */
    @Column(nullable = false)
    private Integer dosage;

    public MedPlanItem() {}

    public MedPlanItem(String medicineName, Integer dosage) {
        this.medicineName = medicineName;
        this.dosage = dosage;
    }

    // ====== Getter / Setter ======

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public MedPlan getPlan() { return plan; }
    public void setPlan(MedPlan plan) { this.plan = plan; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public Integer getDosage() { return dosage; }
    public void setDosage(Integer dosage) { this.dosage = dosage; }
}

