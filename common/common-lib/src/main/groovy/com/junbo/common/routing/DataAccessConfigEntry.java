/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.routing;

/**
 * The policy for data access.
 */
public class DataAccessConfigEntry {
    private Class<?> resourceClass;
    private DataAccessPolicy dataAccessPolicy;

    public Class<?> getResourceClass() {
        return resourceClass;
    }

    public void setResourceClass(Class<?> resourceClass) {
        this.resourceClass = resourceClass;
    }

    public DataAccessPolicy getDataAccessPolicy() {
        return dataAccessPolicy;
    }

    public void setDataAccessPolicy(DataAccessPolicy dataAccessPolicy) {
        this.dataAccessPolicy = dataAccessPolicy;
    }
}
