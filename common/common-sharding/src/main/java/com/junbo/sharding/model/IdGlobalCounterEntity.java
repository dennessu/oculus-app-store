/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by liangfu on 3/5/14.
 */
@Entity
@Table(name = "id_global_counter")
public class IdGlobalCounterEntity {
    @Id
    @GenericGenerator(name="gen",strategy="increment")
    @GeneratedValue(generator="gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "option_mode")
    private Long optionMode;

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

    public Long getOptionMode() {
        return optionMode;
    }

    public void setOptionMode(Long optionMode) {
        this.optionMode = optionMode;
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
