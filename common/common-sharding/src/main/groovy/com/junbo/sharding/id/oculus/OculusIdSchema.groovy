/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.oculus

import com.junbo.configuration.topo.DataCenters
import groovy.transform.CompileStatic

/**
 * Java doc for IdSchema.
 */
@CompileStatic
class OculusIdSchema {
    // 2): Option 2: Id generator proposed by oculus rift(48 version)
    // 3): option 3: order Id generator proposed by oculus rift(40 version)
    private final int idType
    private final int dataCenterId
    private final int bitsInDataCenterId
    private final int idVersion
    private final int bitsInIdVersion
    private final int bitsInShard
    private final int bitsInGlobalCounter
    private final int masksInGlobalCounter
    private final int bitsInLocalCounter
    private final int masksInLocalCounter
    private final int numberOfShards

    OculusIdSchema(int idType, String dataCenter, int bitsInDataCenterId, int bitsInGlobalCounter,
               int bitsInLocalCounter, int idVersion, int bitsInIdVersion, int bitsInShardParam, int numberOfShards) {
        this.idType = idType
        this.bitsInGlobalCounter = bitsInGlobalCounter
        this.masksInGlobalCounter = masks(bitsInGlobalCounter)

        this.bitsInLocalCounter = bitsInLocalCounter
        this.masksInLocalCounter = masks(bitsInLocalCounter)
        this.numberOfShards = numberOfShards
        this.bitsInShard = bitsInShardParam

        this.dataCenterId = DataCenters.instance().getDataCenter(dataCenter).id
        int dataCenterInShard = bits(dataCenterId)
        if (dataCenterInShard < 0 || dataCenterInShard > bitsInDataCenterId) {
            throw new IllegalArgumentException('dataCenterId ' + dataCenterId + ' should be 0..' + bitsInDataCenterId)
        }
        this.bitsInDataCenterId = bitsInDataCenterId

        int idVersionInShard = bits(idVersion)
        if (idVersionInShard < 0 || idVersionInShard > bitsInIdVersion) {
            throw new IllegalArgumentException('idVersion ' + idVersion + ' should be 0..' + bitsInIdVersion)
        }
        this.idVersion = idVersion
        this.bitsInIdVersion = bitsInIdVersion

        int totalBits = bitsInGlobalCounter + bitsInLocalCounter + bitsInShardParam + bitsInIdVersion
        if (totalBits > 63 || totalBits < 0) {
            throw new IllegalArgumentException('totalBits of IdSchema ' + totalBits + ' should be 0..63')
        }
    }

    OculusObjectId parseObjectId(long value) {
        if (value < 0) {
            throw new IllegalArgumentException('ObjectId ' + value + ' should be >= 0')
        }

        int idVersion = (int)(value & masks(bitsInIdVersion))
        value = value >> bitsInIdVersion
        int dataCenterId = (int)((value) & masks(bitsInDataCenterId))

        value = value >> bitsInDataCenterId
        int shardId = (int)(value & masks(bitsInShard))

        value = value >> bitsInShard
        long localCounter = value & masks(bitsInLocalCounter)

        value = value >> bitsInLocalCounter
        long globalCounter = value & masks(bitsInGlobalCounter)

        return new OculusObjectId(idVersion, dataCenterId, shardId, localCounter, globalCounter)
    }

    int getBitsInGlobalCounter() {
        return bitsInGlobalCounter
    }

    int getMasksInGlobalCounter() {
        return masksInGlobalCounter
    }

    int getBitsInLocalCounter() {
        return bitsInLocalCounter
    }

    int getMasksInLocalCounter() {
        return masksInLocalCounter
    }

    int getDataCenterId() {
        return dataCenterId
    }

    int getBitsInDataCenterId() {
        return bitsInDataCenterId
    }

    int getIdVersion() {
        return idVersion
    }

    int getBitsInIdVersion() {
        return bitsInIdVersion
    }

    int getNumberOfShards() {
        return numberOfShards
    }

    int getBitsInShard() {
        return bitsInShard
    }

    int getIdType() {
        return idType
    }

    private static int masks(int bits) {
        return (int) ((1L << bits) - 1)
    }

    private static int bits(int number) {
        int bits = 0
        while ((1L << bits) < number) {
            bits++
        }
        return bits
    }
}

