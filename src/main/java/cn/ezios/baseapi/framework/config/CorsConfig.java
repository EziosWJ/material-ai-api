package cn.ezios.baseapi.framework.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域资源共享（CORS）配置。
 * <p>允许前端跨域访问 {@code /api/**} 下的所有接口。</p>
 */
@Configuration
public class CorsConfig {

    /**
     * 注册 CORS 过滤器，根据配置的允许来源列表设置跨域策略。
     *
     * @param origins 允许的来源地址，多个以逗号分隔，默认为 http://localhost:5173
     * @return CORS 过滤器实例
     */
    @Bean
    public CorsFilter corsFilter(@Value("${app.cors.allowed-origins:http://localhost:5173}") String origins) {
        CorsConfiguration config = new CorsConfiguration();
        for (String origin : origins.split(",")) {
            String trimmed = origin.trim();
            if (!trimmed.isEmpty()) {
                config.addAllowedOrigin(trimmed);
            }
        }
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
