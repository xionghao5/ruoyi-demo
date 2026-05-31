package com.ruoyi.ai.controller;

import java.util.List;
import java.util.Map;

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

import com.ruoyi.ai.domain.AiVectorData;
import com.ruoyi.ai.domain.AiVectorStore;
import com.ruoyi.ai.service.AiVectorDbService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 向量数据库控制器
 * 提供向量库管理、向量数据CRUD和向量检索的REST API
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/ai/vector")
public class AiVectorDbController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(AiVectorDbController.class);

    @Autowired
    private AiVectorDbService vectorDbService;

    // ==================== 向量库管理 ====================

    /**
     * 查询所有向量库
     */
    @RequiresPermissions("ai:vector:view")
    @GetMapping("/stores")
    @ResponseBody
    public AjaxResult listStores()
    {
        List<AiVectorStore> stores = vectorDbService.listStores();
        return AjaxResult.success(stores);
    }

    /**
     * 获取向量库详情
     */
    @RequiresPermissions("ai:vector:view")
    @GetMapping("/store")
    @ResponseBody
    public AjaxResult getStore(@RequestParam("storeName") String storeName)
    {
        AiVectorStore store = vectorDbService.getStore(storeName);
        if (store == null)
        {
            return AjaxResult.error("向量库不存在：" + storeName);
        }
        return AjaxResult.success(store);
    }

    /**
     * 创建向量库
     */
    @RequiresPermissions("ai:vector:add")
    @PostMapping("/createStore")
    @ResponseBody
    public AjaxResult createStore(@RequestParam("storeName") String storeName,
                                  @RequestParam(value = "description", required = false) String description,
                                  @RequestParam(value = "dimension", required = false, defaultValue = "1024") int dimension)
    {
        try
        {
            AiVectorStore store = vectorDbService.createStore(storeName, description, dimension);
            return AjaxResult.success(store);
        }
        catch (IllegalArgumentException e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 删除向量库
     */
    @RequiresPermissions("ai:vector:remove")
    @PostMapping("/deleteStore")
    @ResponseBody
    public AjaxResult deleteStore(@RequestParam("storeName") String storeName)
    {
        boolean result = vectorDbService.deleteStore(storeName);
        return result ? AjaxResult.success("删除成功") : AjaxResult.error("向量库不存在：" + storeName);
    }

    // ==================== 向量数据CRUD ====================

    /**
     * 查询向量库中所有数据
     */
    @RequiresPermissions("ai:vector:view")
    @GetMapping("/data")
    @ResponseBody
    public AjaxResult listData(@RequestParam("storeName") String storeName)
    {
        try
        {
            List<AiVectorData> dataList = vectorDbService.listData(storeName);
            return AjaxResult.success(dataList);
        }
        catch (IllegalArgumentException e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 获取单条向量数据
     */
    @RequiresPermissions("ai:vector:view")
    @GetMapping("/get")
    @ResponseBody
    public AjaxResult getData(@RequestParam("storeName") String storeName,
                              @RequestParam("id") String id)
    {
        AiVectorData data = vectorDbService.get(storeName, id);
        if (data == null)
        {
            return AjaxResult.error("向量数据不存在");
        }
        return AjaxResult.success(data);
    }

    /**
     * 插入向量数据（自动生成向量）
     */
    @RequiresPermissions("ai:vector:add")
    @PostMapping("/insert")
    @ResponseBody
    public AjaxResult insert(@RequestParam("storeName") String storeName,
                             @RequestParam(value = "id", required = false) String id,
                             @RequestParam("content") String content)
    {
        try
        {
            AiVectorData data = vectorDbService.insert(storeName, id, content);
            return AjaxResult.success(data);
        }
        catch (Exception e)
        {
            log.error("插入向量数据失败", e);
            return AjaxResult.error("插入失败：" + e.getMessage());
        }
    }

    /**
     * 更新向量数据（重新生成向量）
     */
    @RequiresPermissions("ai:vector:edit")
    @PostMapping("/update")
    @ResponseBody
    public AjaxResult update(@RequestParam("storeName") String storeName,
                             @RequestParam("id") String id,
                             @RequestParam("content") String content)
    {
        try
        {
            AiVectorData data = vectorDbService.update(storeName, id, content, null);
            return AjaxResult.success(data);
        }
        catch (Exception e)
        {
            log.error("更新向量数据失败", e);
            return AjaxResult.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除向量数据
     */
    @RequiresPermissions("ai:vector:remove")
    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(@RequestParam("storeName") String storeName,
                             @RequestParam("id") String id)
    {
        boolean result = vectorDbService.delete(storeName, id);
        return result ? AjaxResult.success("删除成功") : AjaxResult.error("向量数据不存在");
    }

    // ==================== 向量检索 ====================

    /**
     * 基于文本的向量检索
     */
    @RequiresPermissions("ai:vector:view")
    @GetMapping("/search")
    @ResponseBody
    public AjaxResult search(@RequestParam("storeName") String storeName,
                             @RequestParam("query") String query,
                             @RequestParam(value = "topK", required = false, defaultValue = "5") int topK,
                             @RequestParam(value = "minScore", required = false, defaultValue = "0") double minScore)
    {
        try
        {
            List<AiVectorData> results;
            if (minScore > 0)
            {
                results = vectorDbService.search(storeName, query, topK, minScore);
            }
            else
            {
                results = vectorDbService.search(storeName, query, topK);
            }
            return AjaxResult.success(results);
        }
        catch (Exception e)
        {
            log.error("向量检索失败", e);
            return AjaxResult.error("检索失败：" + e.getMessage());
        }
    }

    /**
     * 获取向量库数据条数
     */
    @RequiresPermissions("ai:vector:view")
    @GetMapping("/count")
    @ResponseBody
    public AjaxResult count(@RequestParam("storeName") String storeName)
    {
        int count = vectorDbService.count(storeName);
        return AjaxResult.success(count);
    }
}
