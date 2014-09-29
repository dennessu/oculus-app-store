/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.id.oculus

import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/21/14.
 */
@CompileStatic
class OculusObjectId {
    private final int idVersion
    private final int dataCenterId
    private final int shardId
    private final long localCounter
    private final long globalCounter

    OculusObjectId(int idVersion, int dataCenterId, int shardId, long localCounter, long globalCounter) {
        this.idVersion = idVersion
        this.dataCenterId = dataCenterId
        this.shardId = shardId
        this.localCounter = localCounter
        this.globalCounter = globalCounter
    }

    int getIdVersion() {
        return idVersion
    }

    int getDataCenterId() {
        return dataCenterId
    }

    int getShardId() {
        return shardId
    }

    long getLocalCounter() {
        return localCounter
    }

    long getGlobalCounter() {
        return globalCounter
    }
}
