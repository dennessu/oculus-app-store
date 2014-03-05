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

    private final int bitsInTime;

    private final int masksInTime;

    private final int timeSecOffset;


    private final int bitsInGlobalCounter;

    private final int masksInGlobalCounter;


    private final int bitsInLocalCounter;

    private final int masksInLocalCounter;


    private final int numberOfShards;

    public IdSchema(int bitsInTime, int timeSecOffset, int bitsInGlobalCounter,
                    int bitsInLocalCounter, int numberOfShards) {
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

    public int getNumberOfShards() {
        return numberOfShards;
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

