package cn.ezios.baseapi.modules.material.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MaterialSaveRequest {

    @NotBlank(message = "材料标题不能为空")
    @Size(max = 200, message = "材料标题长度不能超过 200")
    private String title;

    @NotBlank(message = "原始文件名不能为空")
    @Size(max = 255, message = "原始文件名长度不能超过 255")
    private String originalFilename;

    @NotNull(message = "文件记录不能为空")
    private Long fileId;

    @Size(max = 50, message = "文件类型长度不能超过 50")
    private String fileType;

    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    @Size(max = 32, message = "文件 MD5 长度不能超过 32")
    private String fileMd5;

    @NotBlank(message = "文件存储路径不能为空")
    @Size(max = 500, message = "文件存储路径长度不能超过 500")
    private String storagePath;

    @Size(max = 32, message = "材料状态长度不能超过 32")
    private String status;

    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;
}
