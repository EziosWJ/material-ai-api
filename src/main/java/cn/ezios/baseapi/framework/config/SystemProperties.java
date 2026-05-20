package cn.ezios.baseapi.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 系统业务配置属性。
 * <p>绑定 {@code system.*} 前缀的配置项，提供默认密码、日志清理开关和文件上传相关配置。</p>
 */
@Data
@ConfigurationProperties(prefix = "system")
public class SystemProperties {

    /** 新用户或重置后的默认密码 */
    private String defaultPassword;

    /** 是否启用操作日志自动清理 */
    private boolean logClearEnabled;

    /** 文件上传相关配置 */
    private final FileProperties file = new FileProperties();

    /**
     * 文件上传配置。
     */
    @Data
    public static class FileProperties {

        /** 文件上传根目录，默认为 uploads */
        private String uploadRoot = "uploads";
    }
}
