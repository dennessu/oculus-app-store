/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding;

import java.util.Date;

/**
 * Java doc for IdSchema.
 */
public class IdSchema {
    public static final int INVALID = -1;

    private final int optionMode;

    // 1): Option 1:  Time & memcached server configuration
    private final int bitsInTime;

    private final int masksInTime;

    private final int timeSecOffset;

    // 2): Option 2: Id generator proposed by oculus rift(48 version)

    // 3): option 3: order Id generator proposed by oculus rift(40 version)
    private final int dataCenterId;
    private final int bitsInDataCenterId;

    private final int idVersion;
    private final int bitsInIdVersion;

    private final int idSignificant;
    private final int bitsInIdSignificant;

    private final int bitsInShard;

    // Common parts
    private final int bitsInGlobalCounter;

    private final int masksInGlobalCounter;

    private final int bitsInLocalCounter;

    private final int masksInLocalCounter;


    private final int numberOfShards;

    public IdSchema(int optionMode, int bitsInTime, int timeSecOffset, int bitsInGlobalCounter,
                    int bitsInLocalCounter, int numberOfShards) {
        this.optionMode = optionMode;
        if(optionMode == 1) {
            if (bitsInTime < 0 || bitsInTime > 31) {
                throw new IllegalArgumentException("bitsInTime " + bitsInTime + " should be 0..31");
            }

            this.bitsInTime = bitsInTime;
            this.timeSecOffset = timeSecOffset;
            this.masksInTime = masks(bitsInTime);


            if (bitsInGlobalCounter < 0 || bitsInGlobalCounter > 31) {
                throw new IllegalArgumentException("bitsInGlobalCounter " + bitsInGlobalCounter + " should be 0..31");
            }

            this.bitsInGlobalCounter = bitsInGlobalCounter;
            this.masksInGlobalCounter = masks(bitsInGlobalCounter);


            if (bitsInLocalCounter < 0 || bitsInLocalCounter > 31) {
                throw new IllegalArgumentException("bitsInLocalCounter " + bitsInLocalCounter + " should be 0..31");
            }

            this.bitsInLocalCounter = bitsInLocalCounter;
            this.masksInLocalCounter = masks(bitsInLocalCounter);


            int bitsInShard = bits(numberOfShards);
            if (bitsInShard < 0 || bitsInShard > 31) {
                throw new IllegalArgumentException("numberOfShards " + numberOfShards + " should be 0..31");
            }

            this.numberOfShards = numberOfShards;


            int totalBits = bitsInTime + bitsInGlobalCounter + bitsInLocalCounter + bitsInShard;
            if (totalBits > 63) {
                throw new IllegalArgumentException("totalBits of IdSchema " + totalBits + " should be <= 63");
            }
        }
        else {
            this.bitsInTime = this.timeSecOffset = this.masksInTime = INVALID;
            this.bitsInGlobalCounter = this.masksInGlobalCounter = INVALID;
            this.bitsInLocalCounter = this.masksInLocalCounter = INVALID;
            this.numberOfShards = INVALID;
        }
        this.dataCenterId = INVALID;
        this.bitsInDataCenterId = INVALID;
        this.idVersion = INVALID;
        this.bitsInIdVersion = INVALID;
        this.idSignificant = INVALID;
        this.bitsInIdSignificant = INVALID;
        this.bitsInShard = INVALID;
    }

    public IdSchema(int optionMode, int dataCenterId, int bitsInDataCenterId, int bitsInGlobalCounter,
                    int bitsInLocalCounter, int idVersion, int bitsInIdVersion,
                    int idSignificant, int bitsInIdSignificant, int bitsInShardParam, int numberOfShards) {
        this.optionMode = optionMode;
        if(optionMode == 2) {
            // Oculus 48 version
            if(bitsInGlobalCounter < 0 || bitsInGlobalCounter > 34) {
                throw new IllegalArgumentException("bitsInGlobalCounter " + bitsInGlobalCounter + " should be 0..34.");
            }
            this.bitsInGlobalCounter = bitsInGlobalCounter;
            this.masksInGlobalCounter = masks(bitsInGlobalCounter);

            if(bitsInLocalCounter < 0 || bitsInLocalCounter > 34) {
                throw new IllegalArgumentException("bitsInLocalCounter " + bitsInLocalCounter + " should be 0..34.");
            }
            this.bitsInLocalCounter = bitsInLocalCounter;
            this.masksInLocalCounter = masks(bitsInLocalCounter);
            this.numberOfShards = numberOfShards;
            this.bitsInShard = bitsInShardParam;

            int dataCenterInShard = bits(dataCenterId);
            if(dataCenterInShard < 0 || dataCenterInShard > bitsInDataCenterId) {
                throw new IllegalArgumentException("dataCenterId " + dataCenterId + " should be 0..4");
            }
            if(bitsInDataCenterId < 0 || bitsInDataCenterId > 4) {
                throw new IllegalArgumentException("bitsInDataCenterId " + bitsInDataCenterId + " should be 0..4");
            }
            this.dataCenterId = dataCenterId;
            this.bitsInDataCenterId = bitsInDataCenterId;

            int idVersionInShard = bits(idVersion);
            if(idVersionInShard < 0 || idVersionInShard > bitsInIdVersion) {
                throw new IllegalArgumentException("idVersion " + idVersion + " should be 0..1");
            }
            if(bitsInIdVersion < 0 || bitsInIdVersion > 1) {
                throw new IllegalArgumentException("bitsInIdVersion " + bitsInIdVersion + " should be 0..1");
            }
            this.idVersion = idVersion;
            this.bitsInIdVersion = bitsInIdVersion;

            int idSignificantInShard = bits(idSignificant);
            if(idSignificantInShard < 0 || idSignificantInShard > bitsInIdSignificant) {
                throw new IllegalArgumentException("idSignificant " + idSignificant + " should be 0..1.");
            }
            if(bitsInIdSignificant < 0 || bitsInIdSignificant > 1) {
                throw new IllegalArgumentException("bitsInIdSignificant " + bitsInIdSignificant + " should be 0..1.");
            }
            this.idSignificant = idSignificant;
            this.bitsInIdSignificant = bitsInIdSignificant;

            int totalBits = bitsInGlobalCounter + bitsInLocalCounter +
                    bitsInShardParam + bitsInIdVersion + bitsInIdSignificant;
            if(totalBits > 48) {
                throw new IllegalArgumentException("totalBits of IdSchema " + totalBits + " should be 0..48");
            }
        }
        else if(optionMode == 3) {
            if(bitsInGlobalCounter < 0 || bitsInGlobalCounter > 25) {
                throw new IllegalArgumentException("bitsInGlobalCounter " + bitsInGlobalCounter + " should be 0..25.");
            }
            this.bitsInGlobalCounter = bitsInGlobalCounter;
            this.masksInGlobalCounter = masks(bitsInGlobalCounter);

            if(bitsInLocalCounter < 0 || bitsInLocalCounter > 25) {
                throw new IllegalArgumentException("bitsInLocalCounter " + bitsInLocalCounter + " should be 0..25.");
            }
            this.bitsInLocalCounter = bitsInLocalCounter;
            this.masksInLocalCounter = masks(bitsInLocalCounter);
            this.numberOfShards = numberOfShards;
            this.bitsInShard = bitsInShardParam;

            int dataCenterInShard = bits(dataCenterId);
            if(dataCenterInShard < 0 || dataCenterInShard > bitsInDataCenterId) {
                throw new IllegalArgumentException("dataCenterId " + dataCenterId + " should be 0..4");
            }
            if(bitsInDataCenterId < 0 || bitsInDataCenterId > 4) {
                throw new IllegalArgumentException("bitsInDataCenterId " + bitsInDataCenterId + " should be 0..4");
            }
            this.dataCenterId = dataCenterId;
            this.bitsInDataCenterId = bitsInDataCenterId;

            int idVersionInShard = bits(idVersion);
            if(idVersionInShard < 0 || idVersionInShard > bitsInIdVersion) {
                throw new IllegalArgumentException("idVersion " + idVersion + " should be 0..1");
            }
            if(bitsInIdVersion < 0 || bitsInIdVersion > 1) {
                throw new IllegalArgumentException("bitsInIdVersion " + bitsInIdVersion + " should be 0..1");
            }
            this.idVersion = idVersion;
            this.bitsInIdVersion = bitsInIdVersion;

            int idSignificantInShard = bits(idSignificant);
            if(idSignificantInShard < 0 || idSignificantInShard > bitsInIdSignificant) {
                throw new IllegalArgumentException("idSignificant " + idSignificant + " should be 0..1.");
            }
            if(bitsInIdSignificant < 0 || bitsInIdSignificant > 1) {
                throw new IllegalArgumentException("bitsInIdSignificant " + bitsInIdSignificant + " should be 0..1.");
            }
            this.idSignificant = idSignificant;
            this.bitsInIdSignificant = bitsInIdSignificant;

            int totalBits = bitsInGlobalCounter + bitsInLocalCounter +
                    bitsInShardParam + idVersionInShard + idSignificantInShard;
            if(totalBits > 40) {
                throw new IllegalArgumentException("totalBits of IdSchema " + totalBits + " should be 0..40");
            }
        }
        else {
            this.bitsInGlobalCounter = this.masksInGlobalCounter = this.bitsInLocalCounter = INVALID;
            this.masksInLocalCounter = INVALID;
            this.numberOfShards = this.bitsInShard =  this.dataCenterId = this.bitsInDataCenterId = INVALID;
            this.idVersion = this.bitsInIdVersion = this.idSignificant = this.bitsInIdSignificant = INVALID;
        }
        this.bitsInTime = this.timeSecOffset = this.masksInTime = INVALID;
    }

    public int getBitsInTime() {
        return bitsInTime;
    }

    public int getTimeSecOffset() {
        return timeSecOffset;
    }

    public int getMasksInTime() {
        return masksInTime;
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

    public int getOptionMode() {
        return optionMode;
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

    public int getIdSignificant() {
        return idSignificant;
    }

    public int getBitsInIdSignificant() {
        return bitsInIdSignificant;
    }

    public int getNumberOfShards() {
        return numberOfShards;
    }

    public int getBitsInShard() {
        return bitsInShard;
    }

    public ObjectId parseObjectId(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("ObjectId " + value + " should be >= 0");
        }

        long originalValue = value;

        int shardId = (int) (value % numberOfShards);
        value /= numberOfShards;

        int localCounter = (int) (value & masksInLocalCounter);
        value >>= bitsInLocalCounter;

        int globalCounter = (int) (value & masksInGlobalCounter);
        value >>= bitsInGlobalCounter;

        Date timeSec = new Date(((value & masksInTime) + timeSecOffset) * 1000);
        value >>= bitsInTime;

        assert value == 0 : "value should be = 0";

        return new ObjectId(timeSec, globalCounter, localCounter, shardId, originalValue);
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

