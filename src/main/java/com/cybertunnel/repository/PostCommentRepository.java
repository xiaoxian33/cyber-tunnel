package com.cybertunnel.repository;

import com.cybertunnel.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    /** 获取某条记录的所有评论（时间顺序排列） */
    List<PostComment> findByRecordIdOrderByCreatedAtAsc(Long recordId);

    /** 查询某用户的一条评论（归属校验用：防止删别人的评论） */
    Optional<PostComment> findByIdAndUserId(Long id, Long userId);

    /** 删除某记录的全部评论（删除记录时级联清理） */
    void deleteByRecordId(Long recordId);
}
