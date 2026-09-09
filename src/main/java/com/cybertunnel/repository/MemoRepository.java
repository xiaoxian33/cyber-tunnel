package com.cybertunnel.repository;

import com.cybertunnel.model.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 备忘录/便签 —— 数据访问层
 */
@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {

    /** 按用户查询所有备忘录，按创建时间倒序 */
    List<Memo> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** 查询某用户的一条备忘录（归属校验用：防止删别人的） */
    Optional<Memo> findByIdAndUserId(Long id, Long userId);

    /** 删除用户的所有备忘录 */
    void deleteByUserId(Long userId);
}
