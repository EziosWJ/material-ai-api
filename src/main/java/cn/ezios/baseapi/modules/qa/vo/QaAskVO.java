package cn.ezios.baseapi.modules.qa.vo;

import lombok.Data;

/**
 * 问答结果 VO，包含本次提问的用户消息和 AI 助手回复。
 */
@Data
public class QaAskVO {

    /** 用户发送的问题消息 */
    private QaMessageVO userMessage;

    /** AI 助手的回复消息，包含来源片段 */
    private QaMessageVO assistantMessage;
}
