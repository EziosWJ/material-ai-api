package cn.ezios.baseapi.framework.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import java.util.Map;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "springdoc.api-docs", name = "enabled", havingValue = "true")
public class OpenApiConfig {

    private static final Map<String, String> GROUP_PATHS = Map.of(
            "认证", "/api/auth/**",
            "用户", "/api/system/user/**",
            "角色", "/api/system/role/**",
            "菜单", "/api/system/menu/**",
            "部门", "/api/system/dept/**",
            "字典", "/api/system/dict/**",
            "日志", "/api/system/*-log/**",
            "文件", "/api/system/file/**"
    );

    @Bean
    public OpenAPI baseOpenApi() {
        return new OpenAPI().info(new Info()
                .title("base-api-admin")
                .version("v1.0.0"));
    }

    @Bean
    public GroupedOpenApi authApi() {
        return group("认证");
    }

    @Bean
    public GroupedOpenApi userApi() {
        return group("用户");
    }

    @Bean
    public GroupedOpenApi roleApi() {
        return group("角色");
    }

    @Bean
    public GroupedOpenApi menuApi() {
        return group("菜单");
    }

    @Bean
    public GroupedOpenApi deptApi() {
        return group("部门");
    }

    @Bean
    public GroupedOpenApi dictApi() {
        return group("字典");
    }

    @Bean
    public GroupedOpenApi logApi() {
        return group("日志");
    }

    @Bean
    public GroupedOpenApi fileApi() {
        return group("文件");
    }

    private GroupedOpenApi group(String groupName) {
        return GroupedOpenApi.builder()
                .group(groupName)
                .pathsToMatch(GROUP_PATHS.get(groupName))
                .build();
    }
}
