/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.routing;

import com.junbo.common.routing.model.DataAccessPolicy;

import java.util.HashMap;
import java.util.Map;

/**
 * The policy for data access.
 */
public class DataAccessConfigs {
    private static DataAccessConfigs instance = new DataAccessConfigs();

    public static DataAccessConfigs instance() {
        return instance;
    }

    private Map<String, DataAccessPolicy> data = new HashMap<>();

    public DataAccessPolicy getPolicy(Class<?> clazz, String method) {
        return data.get(method + ":" + clazz.getSimpleName());
    }
}
