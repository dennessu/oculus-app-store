package com.junbo.sharding.hibernate

import com.junbo.common.util.Context
import groovy.transform.CompileStatic

/**
 * Created by Shenhua on 3/31/2014.
 */
@CompileStatic
@SuppressWarnings('CloseWithoutCloseable')
class ShardScope implements AutoCloseable {

    static int currentShardId() {
        Integer currentShardId = Context.get().shardId

        if (currentShardId == null) {
            throw new IllegalStateException('currentShardId is null')
        }

        return currentShardId
    }

    static <T> T withNull(Closure<T> closure) {
        def shardScope = new ShardScope()
        try {
            return closure()
        } finally {
            shardScope.close()
        }
    }

    static <T> T with(Integer shardId, Closure<T> closure) {
        def shardScope = new ShardScope(shardId)
        try {
            return closure()
        } finally {
            shardScope.close()
        }
    }

    private final Integer oldShardId

    ShardScope() {
        this(null)
    }

    ShardScope(Integer shardId) {
        oldShardId = Context.get().shardId
        Context.get().shardId = shardId
    }

    @Override
    void close() {
        Context.get().shardId = oldShardId
    }
}
