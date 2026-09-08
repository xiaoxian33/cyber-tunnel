package com.cybertunnel.repository;

import com.cybertunnel.model.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 备忘录/便签 —— 数据访问层
 */
@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {

    /** 按用户查询所有备忘录，按创建时间倒序 */
    List<Memo> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** 删除用户的所有备忘录 */
    void deleteByUserId(Long userId);
}
