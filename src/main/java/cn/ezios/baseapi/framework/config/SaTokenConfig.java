package cn.ezios.baseapi.framework.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> SaRouter.match("/api/**")
                        .notMatch(EXCLUDE_PATHS)
                        .check(StpUtil::checkLogin)))
                .addPathPatterns("/**");
    }
}
