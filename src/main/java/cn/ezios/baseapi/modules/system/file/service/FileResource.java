package cn.ezios.baseapi.modules.system.file.service;

import org.springframework.core.io.Resource;

/**
 * 文件资源封装
 * <p>用于文件下载和预览时传递资源及相关元信息</p>
 *
 * @param resource     Spring 资源对象
 * @param originalName 原始文件名
 * @param mimeType     MIME类型
 */
public record FileResource(Resource resource, String originalName, String mimeType) {
}
