/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.model;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * Created by liangfu on 3/5/14.
 */
public class ShardIdGlobalCounterEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "shard_id")
    private Long shardId;

    @Column(name = "global_count")
    private Long globalCounter;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShardId() {
        return shardId;
    }

    public void setShardId(Long shardId) {
        this.shardId = shardId;
    }

    public Long getGlobalCounter() {
        return globalCounter;
    }

    public void setGlobalCounter(Long globalCounter) {
        this.globalCounter = globalCounter;
    }
}
