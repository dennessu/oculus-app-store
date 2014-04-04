/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.ds;

/**
 * Created by minhao on 3/8/14.
 */
public class DataSourceConfig {
    private String jdbcUrlTemplate;
    private ShardIdRange range;
    private String loginRole;

    public DataSourceConfig(String jdbcUrlTemplate, String shardIdRange, String loginRole) {
        this.jdbcUrlTemplate = jdbcUrlTemplate;
        this.range = ShardIdRange.parse(shardIdRange);
        this.loginRole = loginRole;
    }

    public String getJdbcUrlTemplate() {
        return jdbcUrlTemplate;
    }

    public ShardIdRange getRange() {
        return range;
    }

    public String getLoginRole() {return loginRole; }

    public boolean contains(int shardId) {
        return this.range.contains(shardId);
    }
}
