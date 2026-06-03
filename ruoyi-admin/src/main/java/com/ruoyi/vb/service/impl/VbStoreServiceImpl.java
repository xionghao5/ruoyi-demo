package com.ruoyi.vb.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.vb.domain.VbStore;
import com.ruoyi.vb.domain.VbVectorData;
import com.ruoyi.vb.mapper.VbStoreMapper;
import com.ruoyi.vb.mapper.VbVectorDataMapper;
import com.ruoyi.vb.service.IVbStoreService;
import com.ruoyi.vb.service.VectorDbService;
import com.ruoyi.common.core.text.Convert;

/**
 * 向量库 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class VbStoreServiceImpl implements IVbStoreService
{
    @Autowired
    private VbStoreMapper storeMapper;

    @Autowired
    private VbVectorDataMapper vectorDataMapper;

    @Autowired
    private VectorDbService vectorDbService;

    @Override
    public VbStore selectStoreById(Long storeId)
    {
        return storeMapper.selectStoreById(storeId);
    }

    @Override
    public List<VbStore> selectStoreList(VbStore store)
    {
        return storeMapper.selectStoreList(store);
    }

    @Override
    public int insertStore(VbStore store)
    {
        int rows = storeMapper.insertStore(store);
        if (rows > 0)
        {
            // 同步到内存向量数据库
            vectorDbService.createStore(store.getStoreId(), store.getStoreName(), store.getDimension());
        }
        return rows;
    }

    @Override
    public int updateStore(VbStore store)
    {
        int rows = storeMapper.updateStore(store);
        if (rows > 0)
        {
            // 同步更新内存中的向量库信息
            vectorDbService.updateStore(store.getStoreId(), store.getStoreName());
        }
        return rows;
    }

    @Override
    public int deleteStoreById(Long storeId)
    {
        // 级联删除向量数据
        vectorDataMapper.deleteDataByStoreId(storeId);
        // 从内存中删除向量库及数据
        vectorDbService.deleteStore(storeId);
        return storeMapper.deleteStoreById(storeId);
    }

    @Override
    public int deleteStoreByIds(String ids)
    {
        String[] storeIds = Convert.toStrArray(ids);
        for (String storeId : storeIds)
        {
            Long sid = Long.valueOf(storeId);
            vectorDataMapper.deleteDataByStoreId(sid);
            vectorDbService.deleteStore(sid);
        }
        return storeMapper.deleteStoreByIds(storeIds);
    }
}
