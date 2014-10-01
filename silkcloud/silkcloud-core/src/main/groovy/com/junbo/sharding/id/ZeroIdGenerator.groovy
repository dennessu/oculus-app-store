/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id

import groovy.transform.CompileStatic
/**
 * Always generate 0. Used for CloudantId.
 */
@CompileStatic
class ZeroIdGenerator implements com.junbo.sharding.IdGenerator {

    @Override
    long nextId() {
        return 0;
    }

    @Override
    long nextId(long id) {
        return 0;
    }

    @Override
    long nextIdByDCIdAndShardId(int dataCenterId, int shardId) {
        return 0;
    }
}
