package cn.ezios.baseapi.modules.ai.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Python AI 服务客户端配置。
 * <p>创建并配置用于调用 Python AI 服务的 {@link RestClient} Bean，设置基础 URL 和超时参数。</p>
 */
@Configuration
@EnableConfigurationProperties(PythonAiProperties.class)
public class PythonAiClientConfig {

    /**
     * 创建 Python AI 服务专用的 RestClient。
     *
     * @param properties         Python AI 服务配置属性
     * @param restTemplateBuilder RestTemplate 构建器
     * @return 配置好的 RestClient 实例
     */
    @Bean
    public RestClient pythonAiRestClient(PythonAiProperties properties, RestTemplateBuilder restTemplateBuilder) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getConnectTimeout());
        requestFactory.setReadTimeout(properties.getReadTimeout());
        return RestClient.builder(restTemplateBuilder.build())
                .baseUrl(properties.getBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}
