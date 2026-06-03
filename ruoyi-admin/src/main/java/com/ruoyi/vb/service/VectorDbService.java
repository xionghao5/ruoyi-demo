package com.ruoyi.vb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ruoyi.vb.domain.VbVectorData;

/**
 * 内存向量数据库服务
 * 支持向量库管理、向量数据CRUD、基于余弦相似度的向量检索
 * 
 * @author ruoyi
 */
@Service
public class VectorDbService
{
    private static final Logger log = LoggerFactory.getLogger(VectorDbService.class);

    /**
     * 内存向量库容器
     * key: storeId
     * value: 向量库信息
     */
    private final ConcurrentHashMap<Long, VectorStore> storeMap = new ConcurrentHashMap<>();

    /**
     * 向量库内部类
     */
    public static class VectorStore
    {
        private Long storeId;
        private String storeName;
        private int dimension;
        private final ConcurrentHashMap<Long, VectorItem> vectors = new ConcurrentHashMap<>();

        public VectorStore(Long storeId, String storeName, int dimension)
        {
            this.storeId = storeId;
            this.storeName = storeName;
            this.dimension = dimension;
        }

        public Long getStoreId()
        {
            return storeId;
        }

        public String getStoreName()
        {
            return storeName;
        }

        public void setStoreName(String storeName)
        {
            this.storeName = storeName;
        }

        public int getDimension()
        {
            return dimension;
        }

        public ConcurrentHashMap<Long, VectorItem> getVectors()
        {
            return vectors;
        }
    }

    /**
     * 向量数据内部类
     */
    public static class VectorItem
    {
        private Long dataId;
        private String content;
        private float[] vector;
        private String metadata;

        public VectorItem(Long dataId, String content, float[] vector, String metadata)
        {
            this.dataId = dataId;
            this.content = content;
            this.vector = vector;
            this.metadata = metadata;
        }

        public Long getDataId()
        {
            return dataId;
        }

        public String getContent()
        {
            return content;
        }

        public void setContent(String content)
        {
            this.content = content;
        }

        public float[] getVector()
        {
            return vector;
        }

        public void setVector(float[] vector)
        {
            this.vector = vector;
        }

        public String getMetadata()
        {
            return metadata;
        }

        public void setMetadata(String metadata)
        {
            this.metadata = metadata;
        }
    }

    // ==================== 向量库管理 ====================

    /**
     * 创建向量库
     */
    public void createStore(Long storeId, String storeName, int dimension)
    {
        VectorStore store = new VectorStore(storeId, storeName, dimension);
        storeMap.put(storeId, store);
        log.info("内存向量库创建成功: storeId={}, storeName={}, dimension={}", storeId, storeName, dimension);
    }

    /**
     * 更新向量库名称
     */
    public void updateStore(Long storeId, String storeName)
    {
        VectorStore store = storeMap.get(storeId);
        if (store != null)
        {
            store.setStoreName(storeName);
            log.info("内存向量库更新成功: storeId={}, storeName={}", storeId, storeName);
        }
    }

    /**
     * 删除向量库（含所有向量数据）
     */
    public void deleteStore(Long storeId)
    {
        VectorStore removed = storeMap.remove(storeId);
        if (removed != null)
        {
            removed.getVectors().clear();
            log.info("内存向量库删除成功: storeId={}", storeId);
        }
    }

    /**
     * 获取向量库
     */
    public VectorStore getStore(Long storeId)
    {
        return storeMap.get(storeId);
    }

    /**
     * 获取所有向量库
     */
    public ConcurrentHashMap<Long, VectorStore> getAllStores()
    {
        return storeMap;
    }

    // ==================== 向量数据管理 ====================

    /**
     * 插入向量数据
     */
    public void insertVector(Long storeId, Long dataId, String content, String embeddingJson, String metadata)
    {
        VectorStore store = storeMap.get(storeId);
        if (store == null)
        {
            log.warn("向量库不存在: storeId={}", storeId);
            return;
        }
        float[] vector = parseVector(embeddingJson);
        if (vector != null && vector.length != store.getDimension())
        {
            log.warn("向量维度不匹配: 期望={}, 实际={}, dataId={}", store.getDimension(), vector.length, dataId);
        }
        VectorItem item = new VectorItem(dataId, content, vector, metadata);
        store.getVectors().put(dataId, item);
        log.debug("向量数据插入内存成功: storeId={}, dataId={}", storeId, dataId);
    }

    /**
     * 更新向量数据
     */
    public void updateVector(Long storeId, Long dataId, String content, String embeddingJson, String metadata)
    {
        VectorStore store = storeMap.get(storeId);
        if (store == null)
        {
            log.warn("向量库不存在: storeId={}", storeId);
            return;
        }
        VectorItem item = store.getVectors().get(dataId);
        if (item != null)
        {
            if (content != null)
            {
                item.setContent(content);
            }
            if (embeddingJson != null)
            {
                float[] vector = parseVector(embeddingJson);
                item.setVector(vector);
            }
            if (metadata != null)
            {
                item.setMetadata(metadata);
            }
            log.debug("向量数据更新内存成功: storeId={}, dataId={}", storeId, dataId);
        }
        else
        {
            // 内存中没有则插入
            insertVector(storeId, dataId, content, embeddingJson, metadata);
        }
    }

    /**
     * 删除向量数据
     */
    public void deleteVector(Long storeId, Long dataId)
    {
        VectorStore store = storeMap.get(storeId);
        if (store != null)
        {
            store.getVectors().remove(dataId);
            log.debug("向量数据从内存删除成功: storeId={}, dataId={}", storeId, dataId);
        }
    }

    /**
     * 删除某向量库下所有向量数据
     */
    public void deleteVectorsByStoreId(Long storeId)
    {
        VectorStore store = storeMap.get(storeId);
        if (store != null)
        {
            store.getVectors().clear();
            log.info("向量库所有数据从内存删除成功: storeId={}", storeId);
        }
    }

    // ==================== 向量检索 ====================

    /**
     * 通过文本向量进行相似度检索
     * 
     * @param storeId 向量库ID
     * @param queryVectorJson 查询向量(JSON数组字符串)
     * @param topK 返回前K个最相似结果
     * @return 按相似度降序排列的向量数据列表
     */
    public List<VbVectorData> searchByVector(Long storeId, String queryVectorJson, int topK)
    {
        float[] queryVector = parseVector(queryVectorJson);
        if (queryVector == null)
        {
            log.warn("查询向量为空或解析失败");
            return Collections.emptyList();
        }
        return searchByVector(storeId, queryVector, topK);
    }

    /**
     * 通过向量数组进行相似度检索
     */
    public List<VbVectorData> searchByVector(Long storeId, float[] queryVector, int topK)
    {
        VectorStore store = storeMap.get(storeId);
        if (store == null)
        {
            log.warn("向量库不存在: storeId={}", storeId);
            return Collections.emptyList();
        }
        if (queryVector == null || queryVector.length == 0)
        {
            return Collections.emptyList();
        }

        List<VbVectorData> results = new ArrayList<>();
        for (VectorItem item : store.getVectors().values())
        {
            if (item.getVector() == null || item.getVector().length == 0)
            {
                continue;
            }
            double similarity = cosineSimilarity(queryVector, item.getVector());
            if (similarity > 0)
            {
                VbVectorData data = new VbVectorData();
                data.setDataId(item.getDataId());
                data.setStoreId(storeId);
                data.setContent(item.getContent());
                data.setMetadata(item.getMetadata());
                data.setScore(similarity);
                results.add(data);
            }
        }

        // 按相似度降序排序
        results.sort(Comparator.comparingDouble(VbVectorData::getScore).reversed());

        // 返回topK结果
        if (results.size() > topK)
        {
            return results.subList(0, topK);
        }
        return results;
    }

    // ==================== 工具方法 ====================

    /**
     * 解析JSON数组字符串为float数组
     */
    public float[] parseVector(String embeddingJson)
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
            log.error("解析向量JSON失败: {}", embeddingJson.substring(0, Math.min(100, embeddingJson.length())), e);
            return null;
        }
    }

    /**
     * 计算余弦相似度
     */
    private double cosineSimilarity(float[] vectorA, float[] vectorB)
    {
        if (vectorA.length != vectorB.length)
        {
            return 0.0;
        }
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++)
        {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }
        if (normA == 0.0 || normB == 0.0)
        {
            return 0.0;
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * 获取向量库中的向量数量
     */
    public int getVectorCount(Long storeId)
    {
        VectorStore store = storeMap.get(storeId);
        return store != null ? store.getVectors().size() : 0;
    }
}
