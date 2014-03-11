/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.oculus;

/**
 * Java doc for IdSchema.
 */
public class OculusIdSchema {
    // 2): Option 2: Id generator proposed by oculus rift(48 version)
    // 3): option 3: order Id generator proposed by oculus rift(40 version)
    private final int idType;
    private final int dataCenterId;
    private final int bitsInDataCenterId;
    private final int idVersion;
    private final int bitsInIdVersion;
    private final int bitsInShard;
    private final int bitsInGlobalCounter;
    private final int masksInGlobalCounter;
    private final int bitsInLocalCounter;
    private final int masksInLocalCounter;
    private final int numberOfShards;

    public OculusIdSchema(int idType, int dataCenterId, int bitsInDataCenterId, int bitsInGlobalCounter,
               int bitsInLocalCounter, int idVersion, int bitsInIdVersion, int bitsInShardParam, int numberOfShards) {
        this.idType = idType;
        this.bitsInGlobalCounter = bitsInGlobalCounter;
        this.masksInGlobalCounter = masks(bitsInGlobalCounter);

        this.bitsInLocalCounter = bitsInLocalCounter;
        this.masksInLocalCounter = masks(bitsInLocalCounter);
        this.numberOfShards = numberOfShards;
        this.bitsInShard = bitsInShardParam;

        int dataCenterInShard = bits(dataCenterId);
        if(dataCenterInShard < 0 || dataCenterInShard > bitsInDataCenterId) {
            throw new IllegalArgumentException("dataCenterId " + dataCenterId + " should be 0.." + bitsInDataCenterId);
        }
        this.dataCenterId = dataCenterId;
        this.bitsInDataCenterId = bitsInDataCenterId;

        int idVersionInShard = bits(idVersion);
        if(idVersionInShard < 0 || idVersionInShard > bitsInIdVersion) {
            throw new IllegalArgumentException("idVersion " + idVersion + " should be 0.." + bitsInIdVersion);
        }
        this.idVersion = idVersion;
        this.bitsInIdVersion = bitsInIdVersion;

        int totalBits = bitsInGlobalCounter + bitsInLocalCounter + bitsInShardParam + bitsInIdVersion;
        if(totalBits > 63 || totalBits < 0) {
            throw new IllegalArgumentException("totalBits of IdSchema " + totalBits + " should be 0..63");
        }
    }

    public int getBitsInGlobalCounter() {
        return bitsInGlobalCounter;
    }

    public int getMasksInGlobalCounter() {
        return masksInGlobalCounter;
    }

    public int getBitsInLocalCounter() {
        return bitsInLocalCounter;
    }

    public int getMasksInLocalCounter() {
        return masksInLocalCounter;
    }

    public int getDataCenterId() {
        return dataCenterId;
    }

    public int getBitsInDataCenterId() {
        return bitsInDataCenterId;
    }

    public int getIdVersion() {
        return idVersion;
    }

    public int getBitsInIdVersion() {
        return bitsInIdVersion;
    }

    public int getNumberOfShards() {
        return numberOfShards;
    }

    public int getBitsInShard() {
        return bitsInShard;
    }

    public int getIdType() {
        return idType;
    }

    private static int masks(int bits) {
        return (int) ((1L << bits) - 1);
    }

    private static int bits(int number) {
        int bits = 0;
        while ((1L << bits) < number) {
            bits++;
        }
        return bits;
    }
}

