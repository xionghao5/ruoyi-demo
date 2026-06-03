package com.ruoyi.vb.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.ruoyi.vb.domain.VbStore;
import com.ruoyi.vb.domain.VbVectorData;
import com.ruoyi.vb.mapper.VbStoreMapper;
import com.ruoyi.vb.mapper.VbVectorDataMapper;

/**
 * 向量数据库启动初始化服务
 * 项目启动时从数据库读取向量库和向量数据载入内存
 * 
 * @author ruoyi
 */
@Component
public class VectorDbInitService implements CommandLineRunner
{
    private static final Logger log = LoggerFactory.getLogger(VectorDbInitService.class);

    @Autowired
    private VbStoreMapper storeMapper;

    @Autowired
    private VbVectorDataMapper vectorDataMapper;

    @Autowired
    private VectorDbService vectorDbService;

    @Override
    public void run(String... args) throws Exception
    {
        log.info("========== 开始加载向量数据库到内存 ==========");
        long startTime = System.currentTimeMillis();

        try
        {
            // 1. 加载所有向量库
            VbStore query = new VbStore();
            query.setStatus("0");
            List<VbStore> stores = storeMapper.selectStoreList(query);
            if (stores == null || stores.isEmpty())
            {
                log.info("未找到向量库数据，跳过加载");
                return;
            }

            int storeCount = 0;
            int vectorCount = 0;

            for (VbStore store : stores)
            {
                // 创建内存向量库
                vectorDbService.createStore(store.getStoreId(), store.getStoreName(), store.getDimension());
                storeCount++;

                // 2. 加载该向量库下的所有向量数据
                List<VbVectorData> dataList = vectorDataMapper.selectDataByStoreId(store.getStoreId());
                if (dataList != null && !dataList.isEmpty())
                {
                    for (VbVectorData data : dataList)
                    {
                        vectorDbService.insertVector(store.getStoreId(), data.getDataId(),
                                data.getContent(), data.getEmbedding(), data.getMetadata());
                        vectorCount++;
                    }
                }
            }

            long elapsed = System.currentTimeMillis() - startTime;
            log.info("========== 向量数据库加载完成: 向量库数={}, 向量数据数={}, 耗时={}ms ==========", storeCount, vectorCount, elapsed);
        }
        catch (Exception e)
        {
            log.error("向量数据库加载失败", e);
        }
    }
}
