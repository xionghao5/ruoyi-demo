package com.ruoyi.ai.mapper;

import java.util.List;
import com.ruoyi.ai.domain.AiKnowledge;

/**
 * AI知识库 数据层
 * 
 * @author ruoyi
 */
public interface AiKnowledgeMapper
{
    public AiKnowledge selectKnowledgeById(Long knowledgeId);

    public List<AiKnowledge> selectKnowledgeList(AiKnowledge knowledge);

    public int insertKnowledge(AiKnowledge knowledge);

    public int updateKnowledge(AiKnowledge knowledge);

    public int deleteKnowledgeById(Long knowledgeId);

    public int deleteKnowledgeByIds(String[] knowledgeIds);
}
