package com.junbo.sharding

/**
 * Created by Shenhua on 4/1/2014.
 */
interface ShardAlgorithm {

    int shardId()

    int shardId(Object key)
}
