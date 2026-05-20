package cn.ezios.baseapi.framework.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 认证拦截配置。
 * <p>对 {@code /api/**} 路径下的请求进行登录校验，白名单路径和 Swagger 文档路径放行。</p>
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /** 不需要登录即可访问的路径白名单 */
    private static final List<String> EXCLUDE_PATHS = List.of(
            "/api/auth/login",
            "/doc.html",
            "/webjars/**",
            "/favicon.ico",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );

    /**
     * 注册 Sa-Token 拦截器，对非白名单的 API 请求执行登录校验。
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> SaRouter.match(
                                SaHttpMethod.GET,
                                SaHttpMethod.POST,
                                SaHttpMethod.PUT,
                                SaHttpMethod.DELETE,
                                SaHttpMethod.PATCH)
                        .match("/api/**")
                        .notMatch(EXCLUDE_PATHS)
                        .check(StpUtil::checkLogin)))
                .addPathPatterns("/**");
    }
}
