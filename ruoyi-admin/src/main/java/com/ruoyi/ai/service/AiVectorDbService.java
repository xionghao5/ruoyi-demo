package com.ruoyi.ai.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ruoyi.ai.domain.AiVectorData;
import com.ruoyi.ai.domain.AiVectorStore;
import com.ruoyi.common.utils.uuid.IdUtils;

/**
 * 简单的向量数据库实现
 * 基于内存的向量存储与检索服务，支持：
 * 1. 创建/删除向量库
 * 2. 插入、修改、删除向量数据
 * 3. 基于余弦相似度的向量检索
 *
 * @author ruoyi
 */
@Service
public class AiVectorDbService
{
    private static final Logger log = LoggerFactory.getLogger(AiVectorDbService.class);

    /** 默认向量维度（text-embedding-v3 输出1024维） */
    private static final int DEFAULT_DIMENSION = 1024;

    /** 向量库存储（storeName -> AiVectorStore） */
    private final ConcurrentHashMap<String, AiVectorStore> storeMap = new ConcurrentHashMap<>();

    @Autowired
    private AiEmbeddingService embeddingService;

    // ==================== 向量库管理 ====================

    /**
     * 创建向量库
     *
     * @param storeName  向量库名称（唯一标识）
     * @param description 向量库描述
     * @param dimension   向量维度
     * @return 创建结果
     */
    public AiVectorStore createStore(String storeName, String description, int dimension)
    {
        if (storeName == null || storeName.trim().isEmpty())
        {
            throw new IllegalArgumentException("向量库名称不能为空");
        }
        if (storeMap.containsKey(storeName))
        {
            throw new IllegalArgumentException("向量库已存在：" + storeName);
        }
        AiVectorStore store = new AiVectorStore(storeName, description, dimension);
        storeMap.put(storeName, store);
        log.info("创建向量库：{}，维度：{}", storeName, dimension);
        return store;
    }

    /**
     * 创建向量库（使用默认维度）
     */
    public AiVectorStore createStore(String storeName, String description)
    {
        return createStore(storeName, description, DEFAULT_DIMENSION);
    }

    /**
     * 删除向量库
     *
     * @param storeName 向量库名称
     * @return 是否删除成功
     */
    public boolean deleteStore(String storeName)
    {
        AiVectorStore removed = storeMap.remove(storeName);
        if (removed != null)
        {
            log.info("删除向量库：{}，包含 {} 条数据", storeName, removed.getDataSize());
            return true;
        }
        return false;
    }

    /**
     * 获取向量库信息
     */
    public AiVectorStore getStore(String storeName)
    {
        return storeMap.get(storeName);
    }

    /**
     * 获取所有向量库列表
     */
    public List<AiVectorStore> listStores()
    {
        return new ArrayList<>(storeMap.values());
    }

    /**
     * 判断向量库是否存在
     */
    public boolean existsStore(String storeName)
    {
        return storeMap.containsKey(storeName);
    }

    // ==================== 向量数据CRUD ====================

    /**
     * 插入向量数据（自动调用嵌入服务生成向量）
     *
     * @param storeName 向量库名称
     * @param id        数据ID（为null时自动生成）
     * @param content   文本内容
     * @param metadata  元数据
     * @return 插入后的向量数据
     */
    public AiVectorData insert(String storeName, String id, String content, Map<String, Object> metadata)
    {
        AiVectorStore store = getRequiredStore(storeName);

        // 调用嵌入服务生成向量
        String embeddingJson = embeddingService.getEmbedding(content);
        float[] vector = parseVector(embeddingJson);

        if (vector == null)
        {
            throw new RuntimeException("生成嵌入向量失败，请检查AI服务配置");
        }

        // 检查向量维度
        if (store.getDimension() > 0 && vector.length != store.getDimension())
        {
            // 自动更新维度
            store.setDimension(vector.length);
        }

        String dataId = (id != null && !id.trim().isEmpty()) ? id : IdUtils.fastSimpleUUID();
        AiVectorData data = new AiVectorData(dataId, storeName, content, vector);
        data.setMetadata(metadata);
        data.setCreateTime(new Date());

        store.getDataMap().put(dataId, data);
        store.setUpdateTime(new Date());
        log.info("插入向量数据：storeName={}，id={}，vectorLength={}", storeName, dataId, vector.length);
        return data;
    }

    /**
     * 插入向量数据（不带元数据）
     */
    public AiVectorData insert(String storeName, String id, String content)
    {
        return insert(storeName, id, content, null);
    }

    /**
     * 插入向量数据（自动生成ID，不带元数据）
     */
    public AiVectorData insert(String storeName, String content)
    {
        return insert(storeName, null, content, null);
    }

    /**
     * 插入向量数据（直接传入向量，不调用嵌入服务）
     *
     * @param storeName 向量库名称
     * @param id        数据ID
     * @param content   文本内容
     * @param vector    向量数据
     * @param metadata  元数据
     * @return 插入后的向量数据
     */
    public AiVectorData insertWithVector(String storeName, String id, String content, float[] vector, Map<String, Object> metadata)
    {
        AiVectorStore store = getRequiredStore(storeName);

        String dataId = (id != null && !id.trim().isEmpty()) ? id : IdUtils.fastSimpleUUID();
        AiVectorData data = new AiVectorData(dataId, storeName, content, vector);
        data.setMetadata(metadata);
        data.setCreateTime(new Date());

        store.getDataMap().put(dataId, data);
        store.setUpdateTime(new Date());
        log.info("插入向量数据（直接传入向量）：storeName={}，id={}", storeName, dataId);
        return data;
    }

    /**
     * 更新向量数据（重新生成向量）
     *
     * @param storeName 向量库名称
     * @param id        数据ID
     * @param content   新文本内容
     * @param metadata  新元数据
     * @return 更新后的向量数据
     */
    public AiVectorData update(String storeName, String id, String content, Map<String, Object> metadata)
    {
        AiVectorStore store = getRequiredStore(storeName);
        AiVectorData existing = store.getDataMap().get(id);
        if (existing == null)
        {
            throw new IllegalArgumentException("向量数据不存在：id=" + id);
        }

        // 重新生成向量
        String embeddingJson = embeddingService.getEmbedding(content);
        float[] vector = parseVector(embeddingJson);

        if (vector == null)
        {
            throw new RuntimeException("生成嵌入向量失败，请检查AI服务配置");
        }

        existing.setContent(content);
        existing.setVector(vector);
        if (metadata != null)
        {
            existing.setMetadata(metadata);
        }
        existing.setUpdateTime(new Date());
        store.setUpdateTime(new Date());
        log.info("更新向量数据：storeName={}，id={}", storeName, id);
        return existing;
    }

    /**
     * 更新向量数据（直接传入向量）
     */
    public AiVectorData updateWithVector(String storeName, String id, String content, float[] vector, Map<String, Object> metadata)
    {
        AiVectorStore store = getRequiredStore(storeName);
        AiVectorData existing = store.getDataMap().get(id);
        if (existing == null)
        {
            throw new IllegalArgumentException("向量数据不存在：id=" + id);
        }

        existing.setContent(content);
        existing.setVector(vector);
        if (metadata != null)
        {
            existing.setMetadata(metadata);
        }
        existing.setUpdateTime(new Date());
        store.setUpdateTime(new Date());
        log.info("更新向量数据（直接传入向量）：storeName={}，id={}", storeName, id);
        return existing;
    }

    /**
     * 删除向量数据
     *
     * @param storeName 向量库名称
     * @param id        数据ID
     * @return 是否删除成功
     */
    public boolean delete(String storeName, String id)
    {
        AiVectorStore store = getRequiredStore(storeName);
        AiVectorData removed = store.getDataMap().remove(id);
        if (removed != null)
        {
            store.setUpdateTime(new Date());
            log.info("删除向量数据：storeName={}，id={}", storeName, id);
            return true;
        }
        return false;
    }

    /**
     * 获取向量数据
     */
    public AiVectorData get(String storeName, String id)
    {
        AiVectorStore store = getRequiredStore(storeName);
        return store.getDataMap().get(id);
    }

    /**
     * 获取向量库中所有数据列表
     */
    public List<AiVectorData> listData(String storeName)
    {
        AiVectorStore store = getRequiredStore(storeName);
        return new ArrayList<>(store.getDataMap().values());
    }

    /**
     * 获取向量库中数据条数
     */
    public int count(String storeName)
    {
        AiVectorStore store = storeMap.get(storeName);
        return store != null ? store.getDataSize() : 0;
    }

    // ==================== 向量检索 ====================

    /**
     * 基于文本的向量检索（自动将查询文本转为向量）
     *
     * @param storeName 向量库名称
     * @param query     查询文本
     * @param topK      返回最相似的前K条结果
     * @return 按相似度降序排列的结果列表
     */
    public List<AiVectorData> search(String storeName, String query, int topK)
    {
        // 将查询文本转为向量
        String embeddingJson = embeddingService.getEmbedding(query);
        float[] queryVector = parseVector(embeddingJson);

        if (queryVector == null)
        {
            log.warn("查询文本向量化失败，返回空结果");
            return Collections.emptyList();
        }

        return searchByVector(storeName, queryVector, topK);
    }

    /**
     * 基于向量的相似度检索
     *
     * @param storeName   向量库名称
     * @param queryVector 查询向量
     * @param topK        返回最相似的前K条结果
     * @return 按相似度降序排列的结果列表
     */
    public List<AiVectorData> searchByVector(String storeName, float[] queryVector, int topK)
    {
        AiVectorStore store = getRequiredStore(storeName);

        List<AiVectorData> results = new ArrayList<>();
        for (AiVectorData data : store.getDataMap().values())
        {
            if (data.getVector() == null || data.getVector().length == 0)
            {
                continue;
            }

            double similarity = cosineSimilarity(queryVector, data.getVector());
            // 创建查询结果副本，避免修改原始数据
            AiVectorData result = new AiVectorData();
            result.setId(data.getId());
            result.setStoreName(data.getStoreName());
            result.setContent(data.getContent());
            result.setMetadata(data.getMetadata());
            result.setScore(similarity);
            result.setCreateTime(data.getCreateTime());
            result.setUpdateTime(data.getUpdateTime());
            results.add(result);
        }

        // 按相似度降序排序
        results.sort(Comparator.comparingDouble(AiVectorData::getScore).reversed());

        // 返回前topK条
        if (topK > 0 && results.size() > topK)
        {
            return results.subList(0, topK);
        }
        return results;
    }

    /**
     * 基于文本的向量检索（带最小相似度阈值）
     *
     * @param storeName     向量库名称
     * @param query         查询文本
     * @param topK          返回最相似的前K条结果
     * @param minScore      最小相似度阈值（0~1）
     * @return 按相似度降序排列的结果列表
     */
    public List<AiVectorData> search(String storeName, String query, int topK, double minScore)
    {
        List<AiVectorData> results = search(storeName, query, topK);
        results.removeIf(data -> data.getScore() < minScore);
        return results;
    }

    // ==================== 工具方法 ====================

    /**
     * 计算余弦相似度
     *
     * @param vectorA 向量A
     * @param vectorB 向量B
     * @return 余弦相似度，范围 [-1, 1]
     */
    private double cosineSimilarity(float[] vectorA, float[] vectorB)
    {
        if (vectorA.length != vectorB.length)
        {
            log.warn("向量维度不匹配：{} vs {}", vectorA.length, vectorB.length);
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++)
        {
            dotProduct += vectorA[i] * vectorB[i];
            normA += (double) vectorA[i] * vectorA[i];
            normB += (double) vectorB[i] * vectorB[i];
        }

        if (normA == 0.0 || normB == 0.0)
        {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * 将JSON格式的向量字符串解析为float数组
     *
     * @param embeddingJson 向量JSON字符串，如 "[0.1, 0.2, 0.3]"
     * @return float数组
     */
    private float[] parseVector(String embeddingJson)
    {
        if (embeddingJson == null || embeddingJson.trim().isEmpty())
        {
            return null;
        }
        try
        {
            JSONArray array = JSON.parseArray(embeddingJson);
            float[] vector = new float[array.size()];
            for (int i = 0; i < array.size(); i++)
            {
                vector[i] = array.getFloatValue(i);
            }
            return vector;
        }
        catch (Exception e)
        {
            log.error("解析向量JSON失败：{}", embeddingJson.substring(0, Math.min(100, embeddingJson.length())), e);
            return null;
        }
    }

    /**
     * 获取向量库，不存在时抛出异常
     */
    private AiVectorStore getRequiredStore(String storeName)
    {
        AiVectorStore store = storeMap.get(storeName);
        if (store == null)
        {
            throw new IllegalArgumentException("向量库不存在：" + storeName);
        }
        return store;
    }
}
