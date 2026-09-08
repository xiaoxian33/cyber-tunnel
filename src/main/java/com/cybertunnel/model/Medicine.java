package com.cybertunnel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 药品实体类 —— 对应数据库中的 medicine 表
 */
@Entity
@Table(name = "medicine")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 外键：该药品属于哪个用户 */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 药品名称（如 "愈美片"） */
    @Column(nullable = false)
    private String name;

    /** 当前库存数量（总剩余片数） */
    @Column(nullable = false)
    private Integer stock = 0;

    /** 是否为常用药 */
    @Column(name = "is_common", nullable = false)
    private Boolean isCommon = false;

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 最后更新时间 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ====== 生命周期回调 ======

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ====== 构造方法 ======

    public Medicine() {
    }

    public Medicine(Long userId, String name, Integer stock, Boolean isCommon) {
        this.userId = userId;
        this.name = name;
        this.stock = stock;
        this.isCommon = isCommon;
    }

    // ====== Getter / Setter ======

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Boolean getIsCommon() { return isCommon; }
    public void setIsCommon(Boolean isCommon) { this.isCommon = isCommon; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

