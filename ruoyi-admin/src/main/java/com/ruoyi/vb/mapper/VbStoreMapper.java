package com.ruoyi.vb.mapper;

import java.util.List;
import com.ruoyi.vb.domain.VbStore;

/**
 * 向量库 数据层
 * 
 * @author ruoyi
 */
public interface VbStoreMapper
{
    public VbStore selectStoreById(Long storeId);

    public List<VbStore> selectStoreList(VbStore store);

    public int insertStore(VbStore store);

    public int updateStore(VbStore store);

    public int deleteStoreById(Long storeId);

    public int deleteStoreByIds(String[] storeIds);
}
