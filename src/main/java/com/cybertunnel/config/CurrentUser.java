package com.cybertunnel.config;

/**
 * 当前请求的"临时储物柜"
 *
 * 为什么需要它？
 *  - 后端同时处理很多用户的请求，必须知道"正在处理的是哪个用户"
 *  - ThreadLocal 的意思：每个请求跑在各自的线程里，各存各的 userId，互不干扰
 *
 * 用法：
 *  - 拦截器验证明白后：CurrentUser.set(userId)
 *  - Controller/Service 里随时：CurrentUser.id() 拿到"当前到底是谁"
 *  - 请求结束：CurrentUser.clear() 清空
 */
public class CurrentUser {

    private static final ThreadLocal<Long> HOLDER = new ThreadLocal<>();

    private CurrentUser() {}

    public static void set(Long userId) {
        HOLDER.set(userId);
    }

    public static Long id() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
