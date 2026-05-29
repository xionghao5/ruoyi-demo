package com.ruoyi.ai.service;

import java.util.List;
import com.ruoyi.ai.domain.AiKnowledge;

/**
 * AI知识库 服务层
 * 
 * @author ruoyi
 */
public interface IAiKnowledgeService
{
    public AiKnowledge selectKnowledgeById(Long knowledgeId);

    public List<AiKnowledge> selectKnowledgeList(AiKnowledge knowledge);

    public int insertKnowledge(AiKnowledge knowledge);

    public int updateKnowledge(AiKnowledge knowledge);

    public int deleteKnowledgeById(Long knowledgeId);

    public int deleteKnowledgeByIds(String ids);

    /**
     * 获取知识库的合并文本内容（用于AI问答注入上下文）
     */
    public String getKnowledgeContent(Long knowledgeId);
}
