package com.ruoyi.ai.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ai.domain.AiKnowledgeFile;
import com.ruoyi.ai.mapper.AiKnowledgeChunkMapper;
import com.ruoyi.ai.mapper.AiKnowledgeFileMapper;
import com.ruoyi.ai.service.IAiKnowledgeFileService;
import com.ruoyi.common.core.text.Convert;

/**
 * AI知识库文件 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class AiKnowledgeFileServiceImpl implements IAiKnowledgeFileService
{
    @Autowired
    private AiKnowledgeFileMapper knowledgeFileMapper;

    @Autowired
    private AiKnowledgeChunkMapper chunkMapper;

    @Override
    public AiKnowledgeFile selectFileById(Long fileId)
    {
        return knowledgeFileMapper.selectFileById(fileId);
    }

    @Override
    public List<AiKnowledgeFile> selectFileByKnowledgeId(Long knowledgeId)
    {
        return knowledgeFileMapper.selectFileByKnowledgeId(knowledgeId);
    }

    @Override
    public int insertFile(AiKnowledgeFile file)
    {
        return knowledgeFileMapper.insertFile(file);
    }

    @Override
    public int deleteFileById(Long fileId)
    {
        // 级联删除文件分块
        chunkMapper.deleteChunksByFileId(fileId);
        return knowledgeFileMapper.deleteFileById(fileId);
    }

    @Override
    public int deleteFileByIds(String ids)
    {
        String[] fileIds = Convert.toStrArray(ids);
        for (String fileId : fileIds)
        {
            chunkMapper.deleteChunksByFileId(Long.valueOf(fileId));
        }
        return knowledgeFileMapper.deleteFileByIds(fileIds);
    }
}
