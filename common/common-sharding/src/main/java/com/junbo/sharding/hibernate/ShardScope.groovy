package com.junbo.sharding.hibernate

import groovy.transform.CompileStatic

/**
 * Created by Shenhua on 3/31/2014.
 */
@CompileStatic
class ShardScope implements AutoCloseable {

    private static final ThreadLocal<Integer> SHARD_ID = new ThreadLocal<>()

    static int currentShardId() {
        Integer currentShardId = SHARD_ID.get()

        if (currentShardId == null) {
            throw new IllegalStateException('currentShardId is null')
        }

        return currentShardId
    }

    private final Integer oldShardId

    ShardScope() {
        this(null)
    }

    ShardScope(Integer shardId) {
        oldShardId = SHARD_ID.get()
        SHARD_ID.set(shardId)
    }

    @Override
    void close() {
        SHARD_ID.set(oldShardId)
    }
}
