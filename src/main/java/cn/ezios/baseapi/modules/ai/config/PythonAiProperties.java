package cn.ezios.baseapi.modules.ai.config;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Python AI 服务连接配置属性。
 * <p>前缀为 {@code ai.python}，支持通过 application.yml 配置服务地址和超时时间。</p>
 */
@Data
@ConfigurationProperties(prefix = "ai.python")
public class PythonAiProperties {

    /** Python AI 服务基础 URL */
    private String baseUrl = "http://127.0.0.1:8000";

    /** 连接超时时间 */
    private Duration connectTimeout = Duration.ofSeconds(3);

    /** 读取超时时间，AI 生成耗时较长，故默认 60 秒 */
    private Duration readTimeout = Duration.ofSeconds(60);
}
