package com.ruoyi.ai.domain;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 向量库（集合）
 * 表示一个命名的向量存储空间，包含多条向量数据
 *
 * @author ruoyi
 */
public class AiVectorStore
{
    /** 向量库名称（唯一标识） */
    private String storeName;

    /** 向量库描述 */
    private String description;

    /** 向量维度 */
    private int dimension;

    /** 向量数据条目数 */
    private int dataSize;

    /** 状态（0正常 1停用） */
    private String status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /** 内部向量数据存储 */
    private transient ConcurrentHashMap<String, AiVectorData> dataMap = new ConcurrentHashMap<>();

    public AiVectorStore()
    {
    }

    public AiVectorStore(String storeName, String description, int dimension)
    {
        this.storeName = storeName;
        this.description = description;
        this.dimension = dimension;
        this.status = "0";
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    public String getStoreName()
    {
        return storeName;
    }

    public void setStoreName(String storeName)
    {
        this.storeName = storeName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getDimension()
    {
        return dimension;
    }

    public void setDimension(int dimension)
    {
        this.dimension = dimension;
    }

    public int getDataSize()
    {
        return dataMap != null ? dataMap.size() : 0;
    }

    public void setDataSize(int dataSize)
    {
        this.dataSize = dataSize;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
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

    public ConcurrentHashMap<String, AiVectorData> getDataMap()
    {
        return dataMap;
    }

    public void setDataMap(ConcurrentHashMap<String, AiVectorData> dataMap)
    {
        this.dataMap = dataMap;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("storeName", getStoreName())
            .append("description", getDescription())
            .append("dimension", getDimension())
            .append("dataSize", getDataSize())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
