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
import com.ruoyi.vb.domain.VbStore;
import com.ruoyi.vb.domain.VbVectorData;
import com.ruoyi.vb.mapper.VbStoreMapper;
import com.ruoyi.vb.mapper.VbVectorDataMapper;
import com.ruoyi.vb.service.IVbStoreService;
import com.ruoyi.vb.service.VectorDbService;
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

    @Autowired
    private IVbStoreService vbStoreService;

    @Autowired
    private VbVectorDataMapper vbVectorDataMapper;

    @Autowired
    private VectorDbService vectorDbService;

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
        int rows = knowledgeMapper.insertKnowledge(knowledge);
        if (rows > 0)
        {
            // 同步创建关联的向量库
            VbStore store = new VbStore();
            store.setStoreName(knowledge.getKnowledgeName());
            store.setDescription("知识库[" + knowledge.getKnowledgeName() + "]关联向量库");
            store.setDimension(1024);
            store.setVectorCount(0);
            store.setStatus("0");
            store.setCreateBy(knowledge.getCreateBy());
            vbStoreService.insertStore(store);

            // 将向量库ID回填到知识库
            knowledge.setStoreId(store.getStoreId());
            knowledgeMapper.updateKnowledge(knowledge);
        }
        return rows;
    }

    @Override
    public int updateKnowledge(AiKnowledge knowledge)
    {
        return knowledgeMapper.updateKnowledge(knowledge);
    }

    @Override
    public int deleteKnowledgeById(Long knowledgeId)
    {
        // 查询知识库获取关联的向量库ID
        AiKnowledge knowledge = knowledgeMapper.selectKnowledgeById(knowledgeId);

        // 级联删除分块和文件
        chunkMapper.deleteChunksByKnowledgeId(knowledgeId);
        knowledgeFileMapper.deleteFileByKnowledgeId(knowledgeId);
        int rows = knowledgeMapper.deleteKnowledgeById(knowledgeId);

        // 同步删除关联的向量库及向量数据
        if (knowledge != null && knowledge.getStoreId() != null)
        {
            vbStoreService.deleteStoreById(knowledge.getStoreId());
        }

        return rows;
    }

    @Override
    public int deleteKnowledgeByIds(String ids)
    {
        String[] knowledgeIds = Convert.toStrArray(ids);
        for (String knowledgeId : knowledgeIds)
        {
            // 查询知识库获取关联的向量库ID
            AiKnowledge knowledge = knowledgeMapper.selectKnowledgeById(Long.valueOf(knowledgeId));

            chunkMapper.deleteChunksByKnowledgeId(Long.valueOf(knowledgeId));
            knowledgeFileMapper.deleteFileByKnowledgeId(Long.valueOf(knowledgeId));

            // 同步删除关联的向量库及向量数据
            if (knowledge != null && knowledge.getStoreId() != null)
            {
                vbStoreService.deleteStoreById(knowledge.getStoreId());
            }
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
