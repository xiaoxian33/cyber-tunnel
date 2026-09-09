package com.cybertunnel.config;

import com.cybertunnel.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

/**
 * 登录拦截器 —— 每个 /api 请求的"安检员"
 *
 * 工作流程：
 *  1. 从请求头 Authorization: Bearer <token> 取出凭证
 *  2. 调用 TokenService.resolve() 把凭证换成 userId
 *  3. 通过 -> 把 userId 放进"当前请求储物柜" CurrentUser，放行进入 Controller
 *  4. 不通过 -> 返回 401 未授权
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public AuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 放行预检请求（浏览器跨域 CORS 会先发 OPTIONS，不需要查证）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 1. 从 Header 取 token："Authorization: Bearer xxxxx"
        String auth = request.getHeader("Authorization");
        String token = null;
        if (auth != null && auth.startsWith("Bearer ")) {
            token = auth.substring("Bearer ".length()).trim();
        }

        // 2. 验证 token -> 换回 userId
        Optional<Long> userId = tokenService.resolve(token);
        if (userId.isEmpty()) {
            // 3. 无效：返回 401，并明确告诉前端原因
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"未登录或登录已过期，请重新登录。\"}");
            return false; // 拦截：请求到此为止，不进 Controller
        }

        // 4. 有效：把真实 userId 放进"当前请求储物柜"
        CurrentUser.set(userId.get());
        return true; // 放行
    }

    /** 请求处理完（无论成功失败）都要清理储物柜，防止用户串数据 */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        CurrentUser.clear();
    }
}
