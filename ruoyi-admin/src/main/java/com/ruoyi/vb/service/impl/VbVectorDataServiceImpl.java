package com.ruoyi.vb.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.vb.domain.VbStore;
import com.ruoyi.vb.domain.VbVectorData;
import com.ruoyi.vb.mapper.VbStoreMapper;
import com.ruoyi.vb.mapper.VbVectorDataMapper;
import com.ruoyi.vb.service.IVbVectorDataService;
import com.ruoyi.vb.service.VectorDbService;
import com.ruoyi.common.core.text.Convert;

/**
 * 向量数据 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class VbVectorDataServiceImpl implements IVbVectorDataService
{
    @Autowired
    private VbVectorDataMapper vectorDataMapper;

    @Autowired
    private VbStoreMapper storeMapper;

    @Autowired
    private VectorDbService vectorDbService;

    @Override
    public VbVectorData selectDataById(Long dataId)
    {
        return vectorDataMapper.selectDataById(dataId);
    }

    @Override
    public List<VbVectorData> selectDataByStoreId(Long storeId)
    {
        return vectorDataMapper.selectDataByStoreId(storeId);
    }

    @Override
    public List<VbVectorData> selectDataList(VbVectorData data)
    {
        return vectorDataMapper.selectDataList(data);
    }

    @Override
    public int insertData(VbVectorData data)
    {
        int rows = vectorDataMapper.insertData(data);
        if (rows > 0)
        {
            // 同步到内存向量数据库
            vectorDbService.insertVector(data.getStoreId(), data.getDataId(),
                    data.getContent(), data.getEmbedding(), data.getMetadata());
            // 更新向量库的向量数量
            updateVectorCount(data.getStoreId(), 1);
        }
        return rows;
    }

    @Override
    public int updateData(VbVectorData data)
    {
        int rows = vectorDataMapper.updateData(data);
        if (rows > 0)
        {
            // 同步更新内存中的向量数据
            vectorDbService.updateVector(data.getStoreId(), data.getDataId(),
                    data.getContent(), data.getEmbedding(), data.getMetadata());
        }
        return rows;
    }

    @Override
    public int deleteDataById(Long dataId)
    {
        VbVectorData existing = vectorDataMapper.selectDataById(dataId);
        int rows = vectorDataMapper.deleteDataById(dataId);
        if (rows > 0 && existing != null)
        {
            // 从内存中删除向量数据
            vectorDbService.deleteVector(existing.getStoreId(), dataId);
            // 更新向量库的向量数量
            updateVectorCount(existing.getStoreId(), -1);
        }
        return rows;
    }

    @Override
    public int deleteDataByStoreId(Long storeId)
    {
        int rows = vectorDataMapper.deleteDataByStoreId(storeId);
        if (rows > 0)
        {
            // 从内存中删除该库所有向量数据
            vectorDbService.deleteVectorsByStoreId(storeId);
            // 重置向量数量
            VbStore store = storeMapper.selectStoreById(storeId);
            if (store != null)
            {
                store.setVectorCount(0);
                storeMapper.updateStore(store);
            }
        }
        return rows;
    }

    @Override
    public int deleteDataByIds(String ids)
    {
        String[] dataIds = Convert.toStrArray(ids);
        for (String dataId : dataIds)
        {
            VbVectorData existing = vectorDataMapper.selectDataById(Long.valueOf(dataId));
            if (existing != null)
            {
                vectorDbService.deleteVector(existing.getStoreId(), Long.valueOf(dataId));
                updateVectorCount(existing.getStoreId(), -1);
            }
        }
        return vectorDataMapper.deleteDataByIds(dataIds);
    }

    /**
     * 更新向量库的向量数量
     */
    private void updateVectorCount(Long storeId, int delta)
    {
        VbStore store = storeMapper.selectStoreById(storeId);
        if (store != null)
        {
            int count = store.getVectorCount() != null ? store.getVectorCount() + delta : delta;
            store.setVectorCount(Math.max(0, count));
            storeMapper.updateStore(store);
        }
    }
}
