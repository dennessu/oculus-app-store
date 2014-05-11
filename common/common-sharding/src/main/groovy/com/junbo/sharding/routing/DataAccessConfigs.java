/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.routing;

import com.junbo.sharding.routing.model.DataAccessConfigEntry;

import java.util.Map;

/**
 * The policy for data access.
 */
public class DataAccessConfigs {
    private Map<Class<?>, DataAccessConfigEntry> data;

    public DataAccessConfigEntry getDataAccessEntry(Class<?> clazz) {
        return data.get(clazz);
    }
}
