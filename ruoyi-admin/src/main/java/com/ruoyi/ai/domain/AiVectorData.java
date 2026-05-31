package com.ruoyi.ai.domain;

import java.util.Date;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 向量数据条目
 * 表示向量数据库中的一条记录，包含文本内容、向量及元数据
 *
 * @author ruoyi
 */
public class AiVectorData
{
    /** 向量数据ID */
    private String id;

    /** 所属向量库名称 */
    private String storeName;

    /** 文本内容 */
    private String content;

    /** 向量数据（浮点数组） */
    private float[] vector;

    /** 元数据（扩展信息，如来源、标签等） */
    private Map<String, Object> metadata;

    /** 相似度分数（仅查询时使用） */
    private Double score;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public AiVectorData()
    {
    }

    public AiVectorData(String id, String storeName, String content, float[] vector)
    {
        this.id = id;
        this.storeName = storeName;
        this.content = content;
        this.vector = vector;
        this.createTime = new Date();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getStoreName()
    {
        return storeName;
    }

    public void setStoreName(String storeName)
    {
        this.storeName = storeName;
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

    public Map<String, Object> getMetadata()
    {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata)
    {
        this.metadata = metadata;
    }

    public Double getScore()
    {
        return score;
    }

    public void setScore(Double score)
    {
        this.score = score;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("storeName", getStoreName())
            .append("content", getContent())
            .append("vectorLength", getVector() != null ? getVector().length : 0)
            .append("metadata", getMetadata())
            .append("score", getScore())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
