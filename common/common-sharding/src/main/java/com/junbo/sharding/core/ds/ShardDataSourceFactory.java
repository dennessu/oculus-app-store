/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.ds;

import javax.sql.DataSource;

/**
 * Created by minhao on 3/8/14.
 */
public interface ShardDataSourceFactory {
    public DataSource createDataSource(ShardDataSourceKey key);
}
