package com.ruoyi.ai.service;

import java.util.List;
import com.ruoyi.ai.domain.AiKnowledgeFile;

/**
 * AI知识库文件 服务层
 * 
 * @author ruoyi
 */
public interface IAiKnowledgeFileService
{
    public AiKnowledgeFile selectFileById(Long fileId);

    public List<AiKnowledgeFile> selectFileByKnowledgeId(Long knowledgeId);

    public int insertFile(AiKnowledgeFile file);

    public int deleteFileById(Long fileId);

    public int deleteFileByIds(String ids);
}
