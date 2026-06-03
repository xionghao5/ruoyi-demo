package com.ruoyi.vb.controller;

import java.util.List;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.vb.domain.VbStore;
import com.ruoyi.vb.domain.VbVectorData;
import com.ruoyi.vb.service.IVbStoreService;
import com.ruoyi.vb.service.IVbVectorDataService;
import com.ruoyi.vb.service.VectorDbService;
import com.ruoyi.ai.service.AiEmbeddingService;

/**
 * 向量数据库控制器
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/vb/store")
public class VbStoreController extends BaseController
{
    private String prefix = "vb";

    @Autowired
    private IVbStoreService storeService;

    /**
     * 向量库管理页面
     */
    @RequiresPermissions("vb:store:view")
    @GetMapping()
    public String store()
    {
        return prefix + "/store";
    }

    @Autowired
    private IVbVectorDataService vectorDataService;

    @Autowired
    private VectorDbService vectorDbService;

    @Autowired
    private AiEmbeddingService embeddingService;

    // ==================== 向量库管理 ====================

    /**
     * 查询向量库列表
     */
    @RequiresPermissions("vb:store:view")
    @GetMapping("/list")
    @ResponseBody
    public TableDataInfo list(VbStore store)
    {
        startPage();
        List<VbStore> list = storeService.selectStoreList(store);
        return getDataTable(list);
    }

    /**
     * 查询向量库详情
     */
    @RequiresPermissions("vb:store:view")
    @GetMapping("/detail")
    @ResponseBody
    public AjaxResult detail(@RequestParam("storeId") Long storeId)
    {
        return AjaxResult.success(storeService.selectStoreById(storeId));
    }

    /**
     * 新增向量库
     */
    @RequiresPermissions("vb:store:add")
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(VbStore store)
    {
        store.setCreateBy(ShiroUtils.getLoginName());
        store.setVectorCount(0);
        store.setStatus("0");
        if (store.getDimension() == null)
        {
            store.setDimension(1536);
        }
        return toAjax(storeService.insertStore(store));
    }

    /**
     * 修改向量库
     */
    @RequiresPermissions("vb:store:edit")
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(VbStore store)
    {
        store.setUpdateBy(ShiroUtils.getLoginName());
        return toAjax(storeService.updateStore(store));
    }

    /**
     * 删除向量库
     */
    @RequiresPermissions("vb:store:remove")
    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(@RequestParam("storeId") Long storeId)
    {
        return toAjax(storeService.deleteStoreById(storeId));
    }

    // ==================== 向量数据管理 ====================

    /**
     * 查询向量数据列表
     */
    @RequiresPermissions("vb:store:view")
    @GetMapping("/data/list")
    @ResponseBody
    public TableDataInfo dataList(VbVectorData data)
    {
        startPage();
        List<VbVectorData> list = vectorDataService.selectDataList(data);
        return getDataTable(list);
    }

    /**
     * 查询某向量库下所有数据
     */
    @RequiresPermissions("vb:store:view")
    @GetMapping("/data/listByStore")
    @ResponseBody
    public AjaxResult dataListByStore(@RequestParam("storeId") Long storeId)
    {
        List<VbVectorData> list = vectorDataService.selectDataByStoreId(storeId);
        return AjaxResult.success(list);
    }

    /**
     * 查询向量数据详情
     */
    @RequiresPermissions("vb:store:view")
    @GetMapping("/data/detail")
    @ResponseBody
    public AjaxResult dataDetail(@RequestParam("dataId") Long dataId)
    {
        return AjaxResult.success(vectorDataService.selectDataById(dataId));
    }

    /**
     * 新增向量数据（自动调用嵌入服务生成向量）
     */
    @RequiresPermissions("vb:store:add")
    @PostMapping("/data/add")
    @ResponseBody
    public AjaxResult dataAddSave(VbVectorData data)
    {
        // 检查向量库是否存在
        VbStore store = storeService.selectStoreById(data.getStoreId());
        if (store == null)
        {
            return AjaxResult.error("向量库不存在");
        }

        data.setCreateBy(ShiroUtils.getLoginName());
        data.setStatus("0");

        // 如果没有提供向量，自动调用嵌入服务生成
        if ((data.getEmbedding() == null || data.getEmbedding().trim().isEmpty())
                && data.getContent() != null && !data.getContent().trim().isEmpty())
        {
            String embedding = embeddingService.getEmbedding(data.getContent());
            if (embedding != null)
            {
                data.setEmbedding(embedding);
            }
        }

        return toAjax(vectorDataService.insertData(data));
    }

    /**
     * 修改向量数据
     */
    @RequiresPermissions("vb:store:edit")
    @PostMapping("/data/edit")
    @ResponseBody
    public AjaxResult dataEditSave(VbVectorData data)
    {
        // 如果内容变更且未提供向量，重新生成嵌入
        if (data.getContent() != null && (data.getEmbedding() == null || data.getEmbedding().trim().isEmpty()))
        {
            String embedding = embeddingService.getEmbedding(data.getContent());
            if (embedding != null)
            {
                data.setEmbedding(embedding);
            }
        }
        data.setUpdateBy(ShiroUtils.getLoginName());
        return toAjax(vectorDataService.updateData(data));
    }

    /**
     * 删除向量数据
     */
    @RequiresPermissions("vb:store:remove")
    @PostMapping("/data/delete")
    @ResponseBody
    public AjaxResult dataDelete(@RequestParam("dataId") Long dataId)
    {
        return toAjax(vectorDataService.deleteDataById(dataId));
    }

    // ==================== 向量检索 ====================

    /**
     * 通过文本进行相似度检索
     * 自动将查询文本转向量，然后在内存中检索
     */
    @RequiresPermissions("vb:store:view")
    @GetMapping("/searchByText")
    @ResponseBody
    public AjaxResult searchByText(@RequestParam("storeId") Long storeId,
                                    @RequestParam("query") String query,
                                    @RequestParam(value = "topK", defaultValue = "10") int topK)
    {
        // 检查向量库是否存在
        VbStore store = storeService.selectStoreById(storeId);
        if (store == null)
        {
            return AjaxResult.error("向量库不存在");
        }

        // 将查询文本转向量
        String queryEmbedding = embeddingService.getEmbedding(query);
        if (queryEmbedding == null)
        {
            return AjaxResult.error("无法获取查询文本的向量，请检查AI嵌入服务配置");
        }

        // 在内存中进行相似度检索
        List<VbVectorData> results = vectorDbService.searchByVector(storeId, queryEmbedding, topK);
        return AjaxResult.success(results);
    }

    /**
     * 通过向量进行相似度检索
     * 直接传入向量JSON数组进行检索
     */
    @RequiresPermissions("vb:store:view")
    @GetMapping("/searchByVector")
    @ResponseBody
    public AjaxResult searchByVector(@RequestParam("storeId") Long storeId,
                                      @RequestParam("vector") String vectorJson,
                                      @RequestParam(value = "topK", defaultValue = "10") int topK)
    {
        List<VbVectorData> results = vectorDbService.searchByVector(storeId, vectorJson, topK);
        return AjaxResult.success(results);
    }

    /**
     * 获取向量库内存统计信息
     */
    @RequiresPermissions("vb:store:view")
    @GetMapping("/stats")
    @ResponseBody
    public AjaxResult stats(@RequestParam("storeId") Long storeId)
    {
        VectorDbService.VectorStore store = vectorDbService.getStore(storeId);
        if (store == null)
        {
            return AjaxResult.error("向量库不存在或未加载到内存");
        }
        AjaxResult result = AjaxResult.success();
        result.put("storeId", store.getStoreId());
        result.put("storeName", store.getStoreName());
        result.put("dimension", store.getDimension());
        result.put("vectorCount", store.getVectors().size());
        return result;
    }
}
