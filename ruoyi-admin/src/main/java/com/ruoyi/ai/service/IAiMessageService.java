package com.ruoyi.ai.service;

import java.util.List;
import com.ruoyi.ai.domain.AiMessage;

/**
 * AI问答消息 服务层
 * 
 * @author ruoyi
 */
public interface IAiMessageService
{
    /**
     * 查询消息信息
     * 
     * @param messageId 消息ID
     * @return 消息信息
     */
    public AiMessage selectMessageById(Long messageId);

    /**
     * 查询消息列表（按对话ID）
     * 
     * @param conversationId 对话ID
     * @return 消息集合
     */
    public List<AiMessage> selectMessageByConversationId(Long conversationId);

    /**
     * 新增消息
     * 
     * @param message 消息信息
     * @return 结果
     */
    public int insertMessage(AiMessage message);

    /**
     * 删除消息（按对话ID）
     * 
     * @param conversationId 对话ID
     * @return 结果
     */
    public int deleteMessageByConversationId(Long conversationId);

    /**
     * 批量删除消息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteMessageByIds(String ids);
}
