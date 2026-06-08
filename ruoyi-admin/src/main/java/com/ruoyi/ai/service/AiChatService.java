package com.ruoyi.ai.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ruoyi.common.core.domain.AjaxResult;

/**
 * AI智能问答服务接口
 * 
 * @author ruoyi
 */
public interface AiChatService
{
    /**
     * 发送问题并获取AI回答（非流式）
     * 
     * @param message 用户问题
     * @return AI回答结果
     */
    AjaxResult chat(String message);

    /**
     * 流式发送问题并获取AI回答
     * 
     * @param message 用户问题
     * @param conversationId 对话ID（为null时新建对话）
     * @param knowledgeId 知识库ID（为null时不使用知识库）
     * @return SSE流式发射器
     */
    SseEmitter streamChat(String message, Long conversationId, Long knowledgeId);
}
