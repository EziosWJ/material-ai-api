package cn.ezios.baseapi.modules.qa.vo;

import lombok.Data;

/**
 * 问答会话关联的材料概要 VO。
 */
@Data
public class QaMaterialVO {

    /** 材料 ID */
    private Long materialId;

    /** 材料标题 */
    private String title;

    /** 原始文件名 */
    private String originalFilename;
}
