package com.junbo.sharding.test.data.dao;

/**
 * Created by haomin on 14-3-4.
 */
public interface ShardDAO {
    void saveShard(ShardEntity entity);
    ShardEntity getShard(Long id);
    ShardEntity updateShard(ShardEntity entity);
    void deleteShard(Long id);
}
