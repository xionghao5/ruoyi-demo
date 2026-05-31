package com.ruoyi.ai.mapper;

import java.util.List;
import com.ruoyi.ai.domain.AiKnowledgeChunk;

/**
 * AI知识库文件分块 数据层
 * 
 * @author ruoyi
 */
public interface AiKnowledgeChunkMapper
{
    /**
     * 根据分块ID查询
     */
    public AiKnowledgeChunk selectChunkById(Long chunkId);

    /**
     * 根据文件ID查询分块列表
     */
    public List<AiKnowledgeChunk> selectChunksByFileId(Long fileId);

    /**
     * 根据知识库ID查询分块列表
     */
    public List<AiKnowledgeChunk> selectChunksByKnowledgeId(Long knowledgeId);

    /**
     * 新增分块
     */
    public int insertChunk(AiKnowledgeChunk chunk);

    /**
     * 批量新增分块
     */
    public int batchInsertChunks(List<AiKnowledgeChunk> chunks);

    /**
     * 根据文件ID删除分块
     */
    public int deleteChunksByFileId(Long fileId);

    /**
     * 根据知识库ID删除分块
     */
    public int deleteChunksByKnowledgeId(Long knowledgeId);

    /**
     * 根据分块ID删除
     */
    public int deleteChunkById(Long chunkId);
}
