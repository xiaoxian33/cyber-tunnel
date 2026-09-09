package com.cybertunnel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 药品库存表 —— 每种药当前还剩多少片
 * 唯一约束：(user_id, medicine_name) —— 每个用户可以有同名的药，互不干扰
 */
@Entity
@Table(name = "medicine_inventory",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "medicine_name"}))
public class MedicineInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 药品名称（同一用户下唯一） */
    @Column(name = "medicine_name", nullable = false)
    private String medicineName;

    /** 当前库存片数 */
    @Column(nullable = false)
    private Integer stock = 0;

    /** 最后更新时间 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public MedicineInventory() {}

    public MedicineInventory(String medicineName, Integer stock) {
        this.medicineName = medicineName;
        this.stock = stock;
    }

    // ====== Getter / Setter ======

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
