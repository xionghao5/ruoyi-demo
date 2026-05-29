package com.ruoyi.ai.mapper;

import java.util.List;
import com.ruoyi.ai.domain.AiKnowledgeFile;

/**
 * AI知识库文件 数据层
 * 
 * @author ruoyi
 */
public interface AiKnowledgeFileMapper
{
    public AiKnowledgeFile selectFileById(Long fileId);

    public List<AiKnowledgeFile> selectFileByKnowledgeId(Long knowledgeId);

    public int insertFile(AiKnowledgeFile file);

    public int deleteFileByKnowledgeId(Long knowledgeId);

    public int deleteFileById(Long fileId);

    public int deleteFileByIds(String[] fileIds);
}
