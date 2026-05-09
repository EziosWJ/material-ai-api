package cn.ezios.baseapi.modules.qa.vo;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class QaSessionVO {

    private Long id;

    private String title;

    private String status;

    private LocalDateTime lastMessageTime;

    private Integer messageCount;

    private List<QaMaterialVO> materials;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
