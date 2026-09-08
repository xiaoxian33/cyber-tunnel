package com.cybertunnel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用药方案/组合 ——「我常吃的某个组合」，方便一键登记
 * 例如「感冒套餐」包含维生素B1(1片)、感冒灵(2片)、头孢(1粒)
 */
@Entity
@Table(name = "med_plan")
public class MedPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户名ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 方案名称 */
    @Column(name = "plan_name", nullable = false)
    private String planName;

    /** 备注（可选） */
    @Column(columnDefinition = "TEXT")
    private String note;

    /** 该方案包含的药品明细（一对一：一个方案含多种药，各带片数） */
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedPlanItem> items = new ArrayList<>();

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public MedPlan() {}

    // 便捷方法：加一项药品
    public void addItem(MedPlanItem item) {
        item.setPlan(this);
        this.items.add(item);
    }

    // ====== Getter / Setter ======

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public List<MedPlanItem> getItems() { return items; }
    public void setItems(List<MedPlanItem> items) { this.items = items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
