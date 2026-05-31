package com.ruoyi.ai.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * AI嵌入向量服务
 * 调用OpenAI兼容接口进行文本向量化
 * 
 * @author ruoyi
 */
@Service
public class AiEmbeddingService
{
    private static final Logger log = LoggerFactory.getLogger(AiEmbeddingService.class);

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.base-url:https://api.openai.com}")
    private String baseUrl;

    @Value("${ai.embedding-model:text-embedding-v3}")
    private String embeddingModel;

    private final RestTemplate restTemplate = new RestTemplate();

    /** 每批最大请求数量 */
    private static final int BATCH_SIZE = 100;

    /**
     * 批量获取文本的嵌入向量（自动分批）
     * 
     * @param texts 文本列表
     * @return 向量列表，每个向量是JSON字符串
     */
    public List<String> getEmbeddings(List<String> texts)
    {
        List<String> embeddings = new ArrayList<>();
        if (texts == null || texts.isEmpty())
        {
            return embeddings;
        }

        if (apiKey == null || apiKey.trim().isEmpty())
        {
            log.warn("AI服务未配置API Key，无法获取嵌入向量");
            return embeddings;
        }

        // 分批处理，每批最多 BATCH_SIZE 条
        for (int i = 0; i < texts.size(); i += BATCH_SIZE)
        {
            int end = Math.min(i + BATCH_SIZE, texts.size());
            List<String> batch = texts.subList(i, end);
            List<String> batchEmbeddings = requestEmbeddings(batch);
            embeddings.addAll(batchEmbeddings);
        }

        return embeddings;
    }

    /**
     * 单次请求获取嵌入向量
     */
    private List<String> requestEmbeddings(List<String> texts)
    {
        List<String> embeddings = new ArrayList<>();
        try
        {
            String url = baseUrl + "/v1/embeddings";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", embeddingModel);
            requestBody.put("input", texts);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null)
            {
                JSONObject jsonObject = JSON.parseObject(response.getBody());
                JSONArray data = jsonObject.getJSONArray("data");
                if (data != null && !data.isEmpty())
                {
                    // 按index排序，确保顺序一致
                    List<JSONObject> sortedList = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++)
                    {
                        sortedList.add(data.getJSONObject(i));
                    }
                    sortedList.sort((a, b) -> Integer.compare(a.getIntValue("index"), b.getIntValue("index")));

                    for (JSONObject item : sortedList)
                    {
                        JSONArray embeddingArray = item.getJSONArray("embedding");
                        if (embeddingArray != null)
                        {
                            embeddings.add(embeddingArray.toJSONString());
                        }
                        else
                        {
                            embeddings.add(null);
                        }
                    }
                }
            }
            else
            {
                log.error("嵌入向量请求失败，状态码：{}，响应：{}", response.getStatusCodeValue(), response.getBody());
            }
        }
        catch (Exception e)
        {
            log.error("获取嵌入向量异常", e);
        }

        return embeddings;
    }

    /**
     * 获取单条文本的嵌入向量
     * 
     * @param text 文本
     * @return 向量JSON字符串
     */
    public String getEmbedding(String text)
    {
        List<String> texts = new ArrayList<>();
        texts.add(text);
        List<String> embeddings = getEmbeddings(texts);
        if (embeddings != null && !embeddings.isEmpty())
        {
            return embeddings.get(0);
        }
        return null;
    }
}
