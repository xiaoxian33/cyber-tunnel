package com.cybertunnel.repository;

import com.cybertunnel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户 —— 数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** 按用户名查找用户（用户名全局唯一） */
    Optional<User> findByUsername(String username);

    /** 判断用户名是否已存在 */
    boolean existsByUsername(String username);

    /** 查询所有用户 */
    List<User> findAllByOrderByCreatedAtAsc();
}
