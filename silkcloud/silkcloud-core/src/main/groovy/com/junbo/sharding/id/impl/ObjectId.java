/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Java doc for Object Id.
 */
public class ObjectId {

    private final Date time;

    private final int globalCounter;

    private final int localCounter;

    private final int shardId;

    private final long value;

    public ObjectId(Date time, int globalCounter, int localCounter, int shardId, long value) {
        this.time = time;
        this.globalCounter = globalCounter;
        this.localCounter = localCounter;
        this.shardId = shardId;
        this.value = value;
    }

    public Date getTime() {
        return time;
    }

    public int getGlobalCounter() {
        return globalCounter;
    }

    public int getLocalCounter() {
        return localCounter;
    }

    public int getShardId() {
        return shardId;
    }

    public long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ObjectId objectId = (ObjectId) o;

        if (value != objectId.value) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public String toString() {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setTimeZone(tz);

        return "ObjectId{" +
                "time=" + df.format(time) +
                ", globalCounter=" + globalCounter +
                ", localCounter=" + localCounter +
                ", shardId=" + shardId +
                ", value=" + value +
                '}';
    }
}

