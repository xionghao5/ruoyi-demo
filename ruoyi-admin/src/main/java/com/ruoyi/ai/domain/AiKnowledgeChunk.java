package com.ruoyi.ai.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * AI知识库文件分块表 ai_knowledge_chunk
 * 
 * @author ruoyi
 */
public class AiKnowledgeChunk
{
    private static final long serialVersionUID = 1L;

    /** 分块ID */
    private Long chunkId;

    /** 文件ID */
    private Long fileId;

    /** 知识库ID */
    private Long knowledgeId;

    /** 文本块内容 */
    private String content;

    /** 向量数据(JSON数组) */
    private String embedding;

    /** 分块序号 */
    private Integer chunkIndex;

    /** 状态（0正常 1失败） */
    private String status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    public Long getChunkId()
    {
        return chunkId;
    }

    public void setChunkId(Long chunkId)
    {
        this.chunkId = chunkId;
    }

    public Long getFileId()
    {
        return fileId;
    }

    public void setFileId(Long fileId)
    {
        this.fileId = fileId;
    }

    public Long getKnowledgeId()
    {
        return knowledgeId;
    }

    public void setKnowledgeId(Long knowledgeId)
    {
        this.knowledgeId = knowledgeId;
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

    public Integer getChunkIndex()
    {
        return chunkIndex;
    }

    public void setChunkIndex(Integer chunkIndex)
    {
        this.chunkIndex = chunkIndex;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("chunkId", getChunkId())
            .append("fileId", getFileId())
            .append("knowledgeId", getKnowledgeId())
            .append("content", getContent())
            .append("embedding", getEmbedding())
            .append("chunkIndex", getChunkIndex())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}
