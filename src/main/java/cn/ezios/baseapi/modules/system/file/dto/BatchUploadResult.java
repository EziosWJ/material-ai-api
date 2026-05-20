package cn.ezios.baseapi.modules.system.file.dto;

import cn.ezios.baseapi.modules.system.file.vo.FileVO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 批量上传结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchUploadResult {

    /** 上传成功的文件列表 */
    private List<FileVO> succeeded;

    /** 上传失败的文件列表 */
    private List<FailedItem> failed;

    /**
     * 上传失败项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailedItem {

        /** 文件名 */
        private String fileName;

        /** 失败原因 */
        private String message;
    }
}
