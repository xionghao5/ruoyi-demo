package com.ruoyi.ai.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ai.domain.AiKnowledge;
import com.ruoyi.ai.domain.AiKnowledgeFile;
import com.ruoyi.ai.mapper.AiKnowledgeChunkMapper;
import com.ruoyi.ai.mapper.AiKnowledgeMapper;
import com.ruoyi.ai.mapper.AiKnowledgeFileMapper;
import com.ruoyi.ai.service.IAiKnowledgeService;
import com.ruoyi.common.core.text.Convert;

/**
 * AI知识库 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class AiKnowledgeServiceImpl implements IAiKnowledgeService
{
    @Autowired
    private AiKnowledgeMapper knowledgeMapper;

    @Autowired
    private AiKnowledgeFileMapper knowledgeFileMapper;

    @Autowired
    private AiKnowledgeChunkMapper chunkMapper;

    @Override
    public AiKnowledge selectKnowledgeById(Long knowledgeId)
    {
        return knowledgeMapper.selectKnowledgeById(knowledgeId);
    }

    @Override
    public List<AiKnowledge> selectKnowledgeList(AiKnowledge knowledge)
    {
        return knowledgeMapper.selectKnowledgeList(knowledge);
    }

    @Override
    public int insertKnowledge(AiKnowledge knowledge)
    {
        return knowledgeMapper.insertKnowledge(knowledge);
    }

    @Override
    public int updateKnowledge(AiKnowledge knowledge)
    {
        return knowledgeMapper.updateKnowledge(knowledge);
    }

    @Override
    public int deleteKnowledgeById(Long knowledgeId)
    {
        // 级联删除分块和文件
        chunkMapper.deleteChunksByKnowledgeId(knowledgeId);
        knowledgeFileMapper.deleteFileByKnowledgeId(knowledgeId);
        return knowledgeMapper.deleteKnowledgeById(knowledgeId);
    }

    @Override
    public int deleteKnowledgeByIds(String ids)
    {
        String[] knowledgeIds = Convert.toStrArray(ids);
        for (String knowledgeId : knowledgeIds)
        {
            chunkMapper.deleteChunksByKnowledgeId(Long.valueOf(knowledgeId));
            knowledgeFileMapper.deleteFileByKnowledgeId(Long.valueOf(knowledgeId));
        }
        return knowledgeMapper.deleteKnowledgeByIds(knowledgeIds);
    }

    @Override
    public String getKnowledgeContent(Long knowledgeId)
    {
        List<AiKnowledgeFile> files = knowledgeFileMapper.selectFileByKnowledgeId(knowledgeId);
        if (files == null || files.isEmpty())
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (AiKnowledgeFile file : files)
        {
            if (file.getContent() != null && !file.getContent().isEmpty())
            {
                sb.append("【文件：").append(file.getFileName()).append("】\n");
                sb.append(file.getContent()).append("\n\n");
            }
        }
        return sb.toString();
    }
}
