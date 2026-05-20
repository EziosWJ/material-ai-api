package cn.ezios.baseapi.modules.qa.vo;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * 问答会话详情 VO，包含会话基本信息和关联材料列表。
 */
@Data
public class QaSessionVO {

    /** 会话 ID */
    private Long id;

    /** 会话标题 */
    private String title;

    /** 会话状态 */
    private String status;

    /** 最后一条消息时间 */
    private LocalDateTime lastMessageTime;

    /** 消息总数 */
    private Integer messageCount;

    /** 关联的材料列表 */
    private List<QaMaterialVO> materials;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
