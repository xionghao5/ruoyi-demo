package com.ruoyi.vb.service;

import java.util.List;
import com.ruoyi.vb.domain.VbVectorData;

/**
 * 向量数据 服务层
 * 
 * @author ruoyi
 */
public interface IVbVectorDataService
{
    public VbVectorData selectDataById(Long dataId);

    public List<VbVectorData> selectDataByStoreId(Long storeId);

    public List<VbVectorData> selectDataList(VbVectorData data);

    public int insertData(VbVectorData data);

    public int updateData(VbVectorData data);

    public int deleteDataById(Long dataId);

    public int deleteDataByStoreId(Long storeId);

    public int deleteDataByIds(String ids);
}
