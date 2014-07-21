package com.junbo.sharding
import com.junbo.configuration.topo.DataCenters
import groovy.transform.CompileStatic
/**
 * Created by Shenhua on 4/1/2014.
 */
@CompileStatic
class DefaultShardAlgorithm implements ShardAlgorithm {

    int shardId(Object key) {
        if (key == null) {
            throw new IllegalArgumentException('key is null')
        }

        if (key instanceof Number) {
            int idValue = ((Number) key).intValue()
            int shardId = (idValue >> 6) & 0xff

            return shardId
        }
        throw new RuntimeException("Cannot handle type: " + key.getClass())
    }

    @Override
    int dataCenterId(Object key) {
        if (key == null) {
            throw new IllegalArgumentException('key is null')
        }

        if (key instanceof Number) {
            int idValue = ((Number) key).intValue()
            int dc = (idValue >> 2) & 0xf

            if (!DataCenters.instance().hasDataCenter(dc)) {
                throw new IllegalArgumentException('dataCenter is invalid with id = ' + dc)
            }

            return dc
        }
        throw new RuntimeException("Cannot handle type: " + key.getClass())
    }
}
