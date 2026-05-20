package cn.ezios.baseapi.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

/**
 * 客户端 IP 地址获取工具类，支持从代理头中提取真实 IP。
 */
public final class IpUtil {

    private static final String UNKNOWN = "unknown";
    /** 按优先级排列的代理头 */
    private static final String[] HEADERS = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};

    private IpUtil() {
    }

    /**
     * 从 HTTP 请求中获取客户端真实 IP 地址。
     * 依次检查常见代理头，取第一个有效值；均无效时回退到 {@code request.getRemoteAddr()}。
     *
     * @param request HTTP 请求
     * @return 客户端 IP 地址
     */
    public static String getClientIp(HttpServletRequest request) {
        for (String header : HEADERS) {
            String value = request.getHeader(header);
            if (StringUtils.hasText(value) && !UNKNOWN.equalsIgnoreCase(value)) {
                return value.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }
}
