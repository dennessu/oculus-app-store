/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.ds;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by haomin on 14-3-10.
 */
public class ShardDataSourceRegistry {
    private Map<ShardDataSourceKey, DataSource> cache = new HashMap<ShardDataSourceKey, DataSource>();
    private ShardDataSourceFactory dataSourceFactory;

    public void setDataSourceFactory(ShardDataSourceFactory factory) { this.dataSourceFactory = factory; }

    public DataSource resolve(int shardId, ShardDataSourceKey key) {
        if (key == null) {
            return null;
        }
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        DataSource ds = this.dataSourceFactory.createDataSource(shardId, key.getDatabaseName());
        cache.put(key, ds);

        return ds;
    }
}
