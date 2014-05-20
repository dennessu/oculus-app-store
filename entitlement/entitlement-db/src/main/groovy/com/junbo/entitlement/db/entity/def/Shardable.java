/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.entity.def;

/**
 * Interface to get ShardId.
 */
public interface Shardable {
    Long getShardMasterId();
}
