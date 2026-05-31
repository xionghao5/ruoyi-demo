package com.ruoyi.ai.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ai.domain.AiKnowledgeChunk;
import com.ruoyi.ai.mapper.AiKnowledgeChunkMapper;
import com.ruoyi.ai.service.IAiKnowledgeChunkService;

/**
 * AI知识库文件分块 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class AiKnowledgeChunkServiceImpl implements IAiKnowledgeChunkService
{
    @Autowired
    private AiKnowledgeChunkMapper chunkMapper;

    @Override
    public AiKnowledgeChunk selectChunkById(Long chunkId)
    {
        return chunkMapper.selectChunkById(chunkId);
    }

    @Override
    public List<AiKnowledgeChunk> selectChunksByFileId(Long fileId)
    {
        return chunkMapper.selectChunksByFileId(fileId);
    }

    @Override
    public List<AiKnowledgeChunk> selectChunksByKnowledgeId(Long knowledgeId)
    {
        return chunkMapper.selectChunksByKnowledgeId(knowledgeId);
    }

    @Override
    public int insertChunk(AiKnowledgeChunk chunk)
    {
        return chunkMapper.insertChunk(chunk);
    }

    @Override
    public int batchInsertChunks(List<AiKnowledgeChunk> chunks)
    {
        return chunkMapper.batchInsertChunks(chunks);
    }

    @Override
    public int deleteChunksByFileId(Long fileId)
    {
        return chunkMapper.deleteChunksByFileId(fileId);
    }

    @Override
    public int deleteChunksByKnowledgeId(Long knowledgeId)
    {
        return chunkMapper.deleteChunksByKnowledgeId(knowledgeId);
    }

    @Override
    public int deleteChunkById(Long chunkId)
    {
        return chunkMapper.deleteChunkById(chunkId);
    }
}
