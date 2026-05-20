package cn.ezios.baseapi.modules.system.file.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 文件元信息更新请求
 */
@Data
public class FileUpdateRequest {

    /** 业务模块标识 */
    @Size(max = 50, message = "业务模块长度不能超过 50")
    private String businessModule;

    /** 备注 */
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;
}
