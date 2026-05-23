package com.ruoyi.ai.service;

import com.ruoyi.common.core.domain.AjaxResult;

/**
 * AI智能问答服务接口
 * 
 * @author ruoyi
 */
public interface AiChatService
{
    /**
     * 发送问题并获取AI回答
     * 
     * @param message 用户问题
     * @return AI回答结果
     */
    AjaxResult chat(String message);
}
