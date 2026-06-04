package com.ruoyi.ai.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * AI知识库表 ai_knowledge
 * 
 * @author ruoyi
 */
public class AiKnowledge extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 知识库ID */
    private Long knowledgeId;

    /** 知识库名称 */
    private String knowledgeName;

    /** 知识库描述 */
    private String description;

    /** 创建用户ID */
    private Long userId;

    /** 关联的向量库ID */
    private Long storeId;

    /** 文件数量 */
    private Integer fileCount;

    /** 状态（0正常 1停用） */
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public Long getKnowledgeId()
    {
        return knowledgeId;
    }

    public void setKnowledgeId(Long knowledgeId)
    {
        this.knowledgeId = knowledgeId;
    }

    public String getKnowledgeName()
    {
        return knowledgeName;
    }

    public void setKnowledgeName(String knowledgeName)
    {
        this.knowledgeName = knowledgeName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getStoreId()
    {
        return storeId;
    }

    public void setStoreId(Long storeId)
    {
        this.storeId = storeId;
    }

    public Integer getFileCount()
    {
        return fileCount;
    }

    public void setFileCount(Integer fileCount)
    {
        this.fileCount = fileCount;
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
            .append("knowledgeId", getKnowledgeId())
            .append("knowledgeName", getKnowledgeName())
            .append("description", getDescription())
            .append("userId", getUserId())
            .append("storeId", getStoreId())
            .append("fileCount", getFileCount())
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
