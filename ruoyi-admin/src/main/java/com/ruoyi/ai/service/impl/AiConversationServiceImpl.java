package com.ruoyi.ai.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ai.domain.AiConversation;
import com.ruoyi.ai.mapper.AiConversationMapper;
import com.ruoyi.ai.mapper.AiMessageMapper;
import com.ruoyi.ai.service.IAiConversationService;
import com.ruoyi.common.core.text.Convert;

/**
 * AI问答对话 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class AiConversationServiceImpl implements IAiConversationService
{
    @Autowired
    private AiConversationMapper conversationMapper;

    @Autowired
    private AiMessageMapper messageMapper;

    /**
     * 查询对话信息
     * 
     * @param conversationId 对话ID
     * @return 对话信息
     */
    @Override
    public AiConversation selectConversationById(Long conversationId)
    {
        return conversationMapper.selectConversationById(conversationId);
    }

    /**
     * 查询对话列表
     * 
     * @param conversation 对话信息
     * @return 对话集合
     */
    @Override
    public List<AiConversation> selectConversationList(AiConversation conversation)
    {
        return conversationMapper.selectConversationList(conversation);
    }

    /**
     * 新增对话
     * 
     * @param conversation 对话信息
     * @return 结果
     */
    @Override
    public int insertConversation(AiConversation conversation)
    {
        return conversationMapper.insertConversation(conversation);
    }

    /**
     * 修改对话
     * 
     * @param conversation 对话信息
     * @return 结果
     */
    @Override
    public int updateConversation(AiConversation conversation)
    {
        return conversationMapper.updateConversation(conversation);
    }

    /**
     * 删除对话
     * 
     * @param conversationId 对话ID
     * @return 结果
     */
    @Override
    public int deleteConversationById(Long conversationId)
    {
        // 先删除该对话下的所有消息
        messageMapper.deleteMessageByConversationId(conversationId);
        return conversationMapper.deleteConversationById(conversationId);
    }

    /**
     * 批量删除对话
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteConversationByIds(String ids)
    {
        String[] conversationIds = Convert.toStrArray(ids);
        // 先删除每个对话下的所有消息
        for (String conversationId : conversationIds)
        {
            messageMapper.deleteMessageByConversationId(Long.valueOf(conversationId));
        }
        return conversationMapper.deleteConversationByIds(conversationIds);
    }
}
