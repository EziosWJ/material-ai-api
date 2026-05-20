package cn.ezios.baseapi.modules.system.file.service;

import cn.ezios.baseapi.common.model.BatchIdsRequest;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.common.model.StatusUpdateRequest;
import cn.ezios.baseapi.modules.system.file.dto.BatchUploadResult;
import cn.ezios.baseapi.modules.system.file.dto.FilePageQuery;
import cn.ezios.baseapi.modules.system.file.dto.FileUpdateRequest;
import cn.ezios.baseapi.modules.system.file.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理服务接口
 * <p>定义文件上传、下载、预览及管理等业务操作</p>
 */
public interface FileService {

    /**
     * 上传单个文件
     *
     * @param file           文件对象
     * @param businessModule 业务模块标识
     * @param remark         备注
     * @return 文件信息
     */
    FileVO upload(MultipartFile file, String businessModule, String remark);

    /**
     * 批量上传文件
     *
     * @param files          文件对象数组
     * @param businessModule 业务模块标识
     * @param remark         备注
     * @return 批量上传结果
     */
    BatchUploadResult uploadBatch(MultipartFile[] files, String businessModule, String remark);

    /**
     * 分页查询文件列表
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResult<FileVO> page(FilePageQuery query);

    /**
     * 获取文件详情
     *
     * @param id 文件ID
     * @return 文件详情
     */
    FileVO getDetail(Long id);

    /**
     * 修改文件元信息
     *
     * @param id      文件ID
     * @param request 文件更新请求
     */
    void update(Long id, FileUpdateRequest request);

    /**
     * 删除文件
     *
     * @param id 文件ID
     */
    void delete(Long id);

    /**
     * 批量删除文件
     *
     * @param request 包含文件ID列表的请求
     */
    void deleteBatch(BatchIdsRequest request);

    /**
     * 修改文件状态
     *
     * @param id      文件ID
     * @param request 状态更新请求
     */
    void updateStatus(Long id, StatusUpdateRequest request);

    /**
     * 下载文件
     *
     * @param id 文件ID
     * @return 文件资源
     */
    FileResource download(Long id);

    /**
     * 预览文件
     *
     * @param id 文件ID
     * @return 文件资源
     */
    FileResource view(Long id);
}
