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

    static int currentDataCenterId() {
        Integer currentDataCenterId = Context.get().dataCenterId

        if (currentDataCenterId == null) {
            throw new IllegalStateException(' currentDataCenterId is null')
        }

        return currentDataCenterId
    }

    static <T> T withNull(Closure<T> closure) {
        def shardScope = new ShardScope()
        try {
            return closure()
        } finally {
            shardScope.close()
        }
    }

    static <T> T with(Integer dataCenterId, Integer shardId, Closure<T> closure) {
        def shardScope = new ShardScope(dataCenterId, shardId)
        try {
            return closure()
        } finally {
            shardScope.close()
        }
    }

    private final Integer oldShardId
    private final Integer oldDataCenterId

    ShardScope() {
        this(null, null)
    }

    ShardScope(Integer dataCenterId, Integer shardId) {
        oldShardId = Context.get().shardId
        oldDataCenterId = Context.get().dataCenterId
        Context.get().shardId = shardId
        Context.get().dataCenterId = dataCenterId
    }

    @Override
    void close() {
        Context.get().shardId = oldShardId
        Context.get().dataCenterId = oldDataCenterId
    }
}
