/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.id.oculus;

/**
 * Created by liangfu on 3/21/14.
 */
public class OculusObjectId {
    private final int idVersion;
    private final int dataCenterId;
    private final int shardId;
    private final long localCounter;
    private final long globalCounter;

    public OculusObjectId(int idVersion, int dataCenterId, int shardId, long localCounter, long globalCounter) {
        this.idVersion = idVersion;
        this.dataCenterId = dataCenterId;
        this.shardId = shardId;
        this.localCounter = localCounter;
        this.globalCounter = globalCounter;
    }

    public int getIdVersion() {
        return idVersion;
    }

    public int getDataCenterId() {
        return dataCenterId;
    }

    public int getShardId() {
        return shardId;
    }

    public long getLocalCounter() {
        return localCounter;
    }

    public long getGlobalCounter() {
        return globalCounter;
    }
}
