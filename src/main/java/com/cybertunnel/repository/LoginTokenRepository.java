package com.cybertunnel.repository;

import com.cybertunnel.model.LoginToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 登录凭证 —— 数据访问层
 */
@Repository
public interface LoginTokenRepository extends JpaRepository<LoginToken, Long> {

    /** 按 token 值查找（token 全局唯一） */
    Optional<LoginToken> findByToken(String token);

    /** 删除某用户的所有旧凭证（用于"重新登录时作废旧证"） */
    void deleteByUserId(Long userId);
}
