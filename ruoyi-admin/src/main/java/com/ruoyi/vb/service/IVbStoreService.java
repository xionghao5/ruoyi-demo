package com.ruoyi.vb.service;

import java.util.List;
import com.ruoyi.vb.domain.VbStore;

/**
 * 向量库 服务层
 * 
 * @author ruoyi
 */
public interface IVbStoreService
{
    public VbStore selectStoreById(Long storeId);

    public List<VbStore> selectStoreList(VbStore store);

    public int insertStore(VbStore store);

    public int updateStore(VbStore store);

    public int deleteStoreById(Long storeId);

    public int deleteStoreByIds(String ids);
}
