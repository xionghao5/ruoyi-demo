package com.ruoyi.ai.mapper;

import java.util.List;
import com.ruoyi.ai.domain.AiConversation;

/**
 * AI问答对话 数据层
 * 
 * @author ruoyi
 */
public interface AiConversationMapper
{
    /**
     * 查询对话信息
     * 
     * @param conversationId 对话ID
     * @return 对话信息
     */
    public AiConversation selectConversationById(Long conversationId);

    /**
     * 查询对话列表
     * 
     * @param conversation 对话信息
     * @return 对话集合
     */
    public List<AiConversation> selectConversationList(AiConversation conversation);

    /**
     * 新增对话
     * 
     * @param conversation 对话信息
     * @return 结果
     */
    public int insertConversation(AiConversation conversation);

    /**
     * 修改对话
     * 
     * @param conversation 对话信息
     * @return 结果
     */
    public int updateConversation(AiConversation conversation);

    /**
     * 删除对话
     * 
     * @param conversationId 对话ID
     * @return 结果
     */
    public int deleteConversationById(Long conversationId);

    /**
     * 批量删除对话
     * 
     * @param conversationIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteConversationByIds(String[] conversationIds);
}
