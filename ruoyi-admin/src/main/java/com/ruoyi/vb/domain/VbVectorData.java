package com.ruoyi.vb.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 向量数据表 vb_vector_data
 * 
 * @author ruoyi
 */
public class VbVectorData extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 数据ID */
    private Long dataId;

    /** 所属向量库ID */
    private Long storeId;

    /** 文本内容 */
    private String content;

    /** 向量数据(JSON数组) */
    private String embedding;

    /** 元数据(JSON) */
    private String metadata;

    /** 状态（0正常 1停用） */
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 相似度分数（非数据库字段，检索时使用） */
    private Double score;

    public Long getDataId()
    {
        return dataId;
    }

    public void setDataId(Long dataId)
    {
        this.dataId = dataId;
    }

    public Long getStoreId()
    {
        return storeId;
    }

    public void setStoreId(Long storeId)
    {
        this.storeId = storeId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getEmbedding()
    {
        return embedding;
    }

    public void setEmbedding(String embedding)
    {
        this.embedding = embedding;
    }

    public String getMetadata()
    {
        return metadata;
    }

    public void setMetadata(String metadata)
    {
        this.metadata = metadata;
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

    public Double getScore()
    {
        return score;
    }

    public void setScore(Double score)
    {
        this.score = score;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("dataId", getDataId())
            .append("storeId", getStoreId())
            .append("content", getContent())
            .append("embedding", getEmbedding())
            .append("metadata", getMetadata())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("score", getScore())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
