package com.junbo.sharding

import com.junbo.configuration.topo.DataCenters
import groovy.transform.CompileStatic

/**
 * Created by Shenhua on 4/1/2014.
 */
@CompileStatic
class DefaultShardByHashAlgorithm implements ShardAlgorithm {

    int shardId(Object key) {
        return hash(key) & 0xff
    }

    int hash(Object key) {
        if (key == null) {
            throw new IllegalArgumentException('key is null')
        }

        if (key instanceof Integer) {
            return (int) key
        }

        if (key instanceof Long) {
            long longKey = (long) key

            return (int) (longKey ^ (longKey >>> 32))
        }

        if (key instanceof String) {
            String strKey = (String) key

            int h = 0
            for (int i = 0; i < strKey.size(); i++) {
                h = 31 * h + ((int) strKey.charAt(i))
            }

            return h
        }

        if (key instanceof UUID) {
            return ((UUID)key).hashCode()
        }

        throw new IllegalArgumentException('unknown key type ' + key.class)
    }

    @Override
    int dataCenterId(Object key) {
        // In this case, it should not have datacenter concept
        return DataCenters.instance().currentDataCenterId();
    }
}

