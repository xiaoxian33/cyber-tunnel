package com.cybertunnel.repository;

import com.cybertunnel.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 药品数据访问层
 *
 * JpaRepository 已内置提供了：
 *   findAll()       查询全部
 *   findById(id)   按主键查单个
 *   save(entity)   新增 / 更新
 *   deleteById(id) 按主键删除
 */
@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    /** 查询某个用户的所有药品 */
    List<Medicine> findByUserId(Long userId);

    /** 根据用户ID + 药品名查找（同一个用户不能有两个同名药） */
    Optional<Medicine> findByUserIdAndName(Long userId, String name);
}
