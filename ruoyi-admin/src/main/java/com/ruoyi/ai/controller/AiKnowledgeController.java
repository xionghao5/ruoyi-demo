package com.ruoyi.ai.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.ruoyi.ai.domain.AiKnowledgeChunk;
import com.ruoyi.ai.service.AiEmbeddingService;
import com.ruoyi.ai.service.IAiKnowledgeChunkService;
import com.ruoyi.common.utils.file.FileReaderUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.ai.domain.AiKnowledge;
import com.ruoyi.ai.domain.AiKnowledgeFile;
import com.ruoyi.ai.service.IAiKnowledgeService;
import com.ruoyi.ai.service.IAiKnowledgeFileService;
import com.ruoyi.common.core.domain.entity.SysUser;

/**
 * AI知识库控制器
 *
 * 知识库-文件-chunk-vector
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/ai/knowledge")
public class AiKnowledgeController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(AiKnowledgeController.class);

    private String prefix = "ai";

    @Autowired
    private IAiKnowledgeService knowledgeService;

    @Autowired
    private IAiKnowledgeFileService knowledgeFileService;

    @Autowired
    private IAiKnowledgeChunkService knowledgeChunkService;

    @Autowired
    private AiEmbeddingService embeddingService;

    @RequiresPermissions("ai:knowledge:view")
    @GetMapping()
    public String knowledge()
    {
        return prefix + "/knowledge";
    }

    /**
     * 查询当前用户的知识库列表
     */
    @RequiresPermissions("ai:knowledge:view")
    @GetMapping("/list")
    @ResponseBody
    public TableDataInfo list(AiKnowledge knowledge)
    {
        startPage();
        SysUser currentUser = ShiroUtils.getSysUser();
        knowledge.setUserId(currentUser.getUserId());
        List<AiKnowledge> list = knowledgeService.selectKnowledgeList(knowledge);
        return getDataTable(list);
    }

    /**
     * 查询所有可用知识库（供问答选择用）
     */
    @RequiresPermissions("ai:chat:view")
    @GetMapping("/all")
    @ResponseBody
    public AjaxResult allList()
    {
        SysUser currentUser = ShiroUtils.getSysUser();
        AiKnowledge query = new AiKnowledge();
        query.setUserId(currentUser.getUserId());
        query.setStatus("0");
        List<AiKnowledge> list = knowledgeService.selectKnowledgeList(query);
        return AjaxResult.success(list);
    }

    /**
     * 新增知识库
     */
    @RequiresPermissions("ai:knowledge:add")
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(AiKnowledge knowledge)
    {
        SysUser currentUser = ShiroUtils.getSysUser();
        knowledge.setUserId(currentUser.getUserId());
        knowledge.setCreateBy(currentUser.getLoginName());
        knowledge.setFileCount(0);
        knowledge.setStatus("0");
        return toAjax(knowledgeService.insertKnowledge(knowledge));
    }

    /**
     * 修改知识库
     */
    @RequiresPermissions("ai:knowledge:edit")
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(AiKnowledge knowledge)
    {
        knowledge.setUpdateBy(ShiroUtils.getLoginName());
        return toAjax(knowledgeService.updateKnowledge(knowledge));
    }

    /**
     * 删除知识库
     */
    @RequiresPermissions("ai:knowledge:remove")
    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(@RequestParam("knowledgeId") Long knowledgeId)
    {
        return toAjax(knowledgeService.deleteKnowledgeById(knowledgeId));
    }

    /**
     * 查询知识库文件列表
     */
    @RequiresPermissions("ai:knowledge:view")
    @GetMapping("/files")
    @ResponseBody
    public AjaxResult files(@RequestParam("knowledgeId") Long knowledgeId)
    {
        List<AiKnowledgeFile> files = knowledgeFileService.selectFileByKnowledgeId(knowledgeId);
        return AjaxResult.success(files);
    }

    /**
     * 上传文件到知识库
     */
    @RequiresPermissions("ai:knowledge:add")
    @PostMapping("/upload")
    @ResponseBody
    public AjaxResult upload(@RequestParam("knowledgeId") Long knowledgeId,
                             @RequestParam("file") MultipartFile file)
    {
        if (file.isEmpty())
        {
            return AjaxResult.error("上传文件不能为空");
        }

        // 文件大小限制 10MB
        if (file.getSize() > 10 * 1024 * 1024)
        {
            return AjaxResult.error("文件大小不能超过10MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename).toLowerCase();

        // 保存文件到磁盘
        String filePath = "";
        String content = "";
        try
        {
            String knowledgePath = RuoYiConfig.getProfile() + "/ai/knowledge/" + knowledgeId;
            File dir = new File(knowledgePath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            String fileName = IdUtils.fastSimpleUUID() + "." + extension;
            filePath = knowledgePath + "/" + fileName;
            file.transferTo(new File(filePath));

            // 提取文件文本内容（支持txt/md/csv等文本格式）
            content = extractTextContent(filePath, extension);
        }
        catch (IOException e)
        {
            log.error("上传文件失败", e);
            return AjaxResult.error("上传文件失败：" + e.getMessage());
        }

        // 保存文件记录
        AiKnowledgeFile knowledgeFile = new AiKnowledgeFile();
        knowledgeFile.setKnowledgeId(knowledgeId);
        knowledgeFile.setFileName(originalFilename);
        knowledgeFile.setFilePath(filePath);
        knowledgeFile.setFileSize(file.getSize());
        knowledgeFile.setFileType(extension);
        knowledgeFile.setContent(content);
        knowledgeFile.setStatus("0");
        knowledgeFile.setCreateBy(ShiroUtils.getLoginName());
        knowledgeFileService.insertFile(knowledgeFile);

        // 文件内容切分并向量化
        if (content != null && !content.trim().isEmpty())
        {
            try
            {
                List<String> chunks = splitContentIntoChunks(content);
                if (!chunks.isEmpty())
                {
                    List<String> embeddings = embeddingService.getEmbeddings(chunks);
                    List<AiKnowledgeChunk> chunkList = new ArrayList<>();
                    for (int i = 0; i < chunks.size(); i++)
                    {
                        AiKnowledgeChunk chunk = new AiKnowledgeChunk();
                        chunk.setFileId(knowledgeFile.getFileId());
                        chunk.setKnowledgeId(knowledgeId);
                        chunk.setContent(chunks.get(i));
                        chunk.setChunkIndex(i);
                        chunk.setStatus("0");
                        if (embeddings != null && i < embeddings.size() && embeddings.get(i) != null)
                        {
                            chunk.setEmbedding(embeddings.get(i));
                        }
                        chunkList.add(chunk);
                    }
                    if (!chunkList.isEmpty())
                    {
                        knowledgeChunkService.batchInsertChunks(chunkList);
                    }
                }
            }
            catch (Exception e)
            {
                log.error("文件切分或向量化失败，fileId={}", knowledgeFile.getFileId(), e);
            }
        }

        // 更新知识库文件计数
        AiKnowledge knowledge = knowledgeService.selectKnowledgeById(knowledgeId);
        if (knowledge != null)
        {
            knowledge.setFileCount(knowledge.getFileCount() != null ? knowledge.getFileCount() + 1 : 1);
            knowledge.setUpdateBy(ShiroUtils.getLoginName());
            knowledgeService.updateKnowledge(knowledge);
        }

        return AjaxResult.success("上传成功");
    }

    /**
     * 删除知识库文件
     */
    @RequiresPermissions("ai:knowledge:remove")
    @PostMapping("/deleteFile")
    @ResponseBody
    public AjaxResult deleteFile(@RequestParam("fileId") Long fileId,
                                 @RequestParam("knowledgeId") Long knowledgeId)
    {
        // 级联删除文件分块
        knowledgeChunkService.deleteChunksByFileId(fileId);
        knowledgeFileService.deleteFileById(fileId);

        // 更新知识库文件计数
        AiKnowledge knowledge = knowledgeService.selectKnowledgeById(knowledgeId);
        if (knowledge != null && knowledge.getFileCount() != null && knowledge.getFileCount() > 0)
        {
            knowledge.setFileCount(knowledge.getFileCount() - 1);
            knowledge.setUpdateBy(ShiroUtils.getLoginName());
            knowledgeService.updateKnowledge(knowledge);
        }

        return AjaxResult.success("删除成功");
    }

    /**
     * 提取文件文本内容
     */
    private String extractTextContent(String filePath, String extension)
    {
        // 支持的文本格式
        if ("txt".equals(extension) || "md".equals(extension) || "csv".equals(extension)
            || "json".equals(extension) || "xml".equals(extension) || "html".equals(extension)
            || "css".equals(extension) || "js".equals(extension) || "java".equals(extension)
            || "py".equals(extension) || "sql".equals(extension) || "yml".equals(extension)
            || "yaml".equals(extension) || "properties".equals(extension) || "log".equals(extension)
            || "sh".equals(extension) || "bat".equals(extension))
        {
            try
            {
                return FileReaderUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            }
            catch (IOException e)
            {
                log.warn("读取文件内容失败: {}", filePath, e);
                return "";
            }
        }
        // 非文本格式暂不支持自动提取，返回提示
        return "[该文件格式（." + extension + "）暂不支持自动提取文本内容，请上传txt/md/csv等文本格式文件]";
    }

    /**
     * 按自然段切分文本，每块不超过512字
     */
    private List<String> splitContentIntoChunks(String content)
    {
        List<String> chunks = new ArrayList<>();
        if (content == null || content.trim().isEmpty())
        {
            return chunks;
        }

        String[] paragraphs = content.split("\\n");
        StringBuilder currentChunk = new StringBuilder();
        final int MAX_LENGTH = 512;

        for (String paragraph : paragraphs)
        {
            String trimmed = paragraph.trim();
            if (trimmed.isEmpty())
            {
                continue;
            }

            // 单个自然段超过最大长度，直接强制切分
            if (trimmed.length() > MAX_LENGTH)
            {
                if (currentChunk.length() > 0)
                {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk.setLength(0);
                }
                for (int i = 0; i < trimmed.length(); i += MAX_LENGTH)
                {
                    int end = Math.min(i + MAX_LENGTH, trimmed.length());
                    chunks.add(trimmed.substring(i, end));
                }
            }
            else
            {
                // 累积自然段，接近512字时形成一个chunk
                if (currentChunk.length() + trimmed.length() + 1 <= MAX_LENGTH)
                {
                    if (currentChunk.length() > 0)
                    {
                        currentChunk.append("\n");
                    }
                    currentChunk.append(trimmed);
                }
                else
                {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk.setLength(0);
                    currentChunk.append(trimmed);
                }
            }
        }

        if (currentChunk.length() > 0)
        {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }
}
