package com.junbo.sharding

/**
 * Created by Shenhua on 4/1/2014.
 */
interface ShardAlgorithm {
    int shardId(Object key)

    int dataCenterId(Object key)
}
