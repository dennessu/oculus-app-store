package com.junbo.sharding

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import java.security.SecureRandom

/**
 * Created by Shenhua on 4/1/2014.
 */
@CompileStatic
class DefaultShardAlgorithm implements ShardAlgorithm {

    private HashAlgorithm hashAlgorithm

    private int availableShards

    private final Random random = new SecureRandom()

    @Required
    void setHashAlgorithm(HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm
    }

    @Required
    void setAvailableShards(int availableShards) {
        this.availableShards = availableShards
    }

    int shardId() {
        return random.nextInt(availableShards)
    }

    int shardId(Object key) {

        int shardId
        if (key instanceof Number) {
            shardId = (((Number) key).intValue() >> 6) & 0xff
        } else {
            shardId = hashAlgorithm.hash(key) & 0xff
        }

        return shardId % availableShards
    }
}
