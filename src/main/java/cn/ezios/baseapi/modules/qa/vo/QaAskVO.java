package cn.ezios.baseapi.modules.qa.vo;

import lombok.Data;

@Data
public class QaAskVO {

    private QaMessageVO userMessage;

    private QaMessageVO assistantMessage;
}
