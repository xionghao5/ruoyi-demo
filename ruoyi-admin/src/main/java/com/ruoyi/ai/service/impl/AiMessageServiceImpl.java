package com.ruoyi.ai.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ai.domain.AiMessage;
import com.ruoyi.ai.mapper.AiMessageMapper;
import com.ruoyi.ai.service.IAiMessageService;
import com.ruoyi.common.core.text.Convert;

/**
 * AI问答消息 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class AiMessageServiceImpl implements IAiMessageService
{
    @Autowired
    private AiMessageMapper messageMapper;

    /**
     * 查询消息信息
     * 
     * @param messageId 消息ID
     * @return 消息信息
     */
    @Override
    public AiMessage selectMessageById(Long messageId)
    {
        return messageMapper.selectMessageById(messageId);
    }

    /**
     * 查询消息列表（按对话ID）
     * 
     * @param conversationId 对话ID
     * @return 消息集合
     */
    @Override
    public List<AiMessage> selectMessageByConversationId(Long conversationId)
    {
        return messageMapper.selectMessageByConversationId(conversationId);
    }

    /**
     * 新增消息
     * 
     * @param message 消息信息
     * @return 结果
     */
    @Override
    public int insertMessage(AiMessage message)
    {
        return messageMapper.insertMessage(message);
    }

    /**
     * 删除消息（按对话ID）
     * 
     * @param conversationId 对话ID
     * @return 结果
     */
    @Override
    public int deleteMessageByConversationId(Long conversationId)
    {
        return messageMapper.deleteMessageByConversationId(conversationId);
    }

    /**
     * 批量删除消息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteMessageByIds(String ids)
    {
        return messageMapper.deleteMessageByIds(Convert.toStrArray(ids));
    }
}
