package com.ruoyi.vb.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 向量库表 vb_store
 * 
 * @author ruoyi
 */
public class VbStore extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 向量库ID */
    private Long storeId;

    /** 向量库名称 */
    private String storeName;

    /** 描述 */
    private String description;

    /** 向量维度 */
    private Integer dimension;

    /** 向量数量 */
    private Integer vectorCount;

    /** 状态（0正常 1停用） */
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public Long getStoreId()
    {
        return storeId;
    }

    public void setStoreId(Long storeId)
    {
        this.storeId = storeId;
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

    public Integer getDimension()
    {
        return dimension;
    }

    public void setDimension(Integer dimension)
    {
        this.dimension = dimension;
    }

    public Integer getVectorCount()
    {
        return vectorCount;
    }

    public void setVectorCount(Integer vectorCount)
    {
        this.vectorCount = vectorCount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("storeId", getStoreId())
            .append("storeName", getStoreName())
            .append("description", getDescription())
            .append("dimension", getDimension())
            .append("vectorCount", getVectorCount())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
