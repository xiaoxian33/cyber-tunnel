package com.cybertunnel.repository;

import com.cybertunnel.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    /** 统计某条记录的点赞数 */
    long countByRecordId(Long recordId);

    /** 查询某用户是否已赞某条记录 */
    Optional<PostLike> findByRecordIdAndUserId(Long recordId, Long userId);

    /** 获取某记录的点赞列表 */
    List<PostLike> findByRecordId(Long recordId);

    /** 删除某记录的全部点赞（删除记录时级联清理） */
    void deleteByRecordId(Long recordId);
}
