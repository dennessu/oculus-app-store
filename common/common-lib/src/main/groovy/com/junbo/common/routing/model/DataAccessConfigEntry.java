/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.routing.model;

/**
 * The policy for data access.
 */
public class DataAccessConfigEntry {
    private Class<?> resourceClass;
    private String httpMethod;
    private DataAccessPolicy dataAccessPolicy;

    public Class<?> getResourceClass() {
        return resourceClass;
    }

    public void setResourceClass(Class<?> resourceClass) {
        this.resourceClass = resourceClass;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public DataAccessPolicy getDataAccessPolicy() {
        return dataAccessPolicy;
    }

    public void setDataAccessPolicy(DataAccessPolicy dataAccessPolicy) {
        this.dataAccessPolicy = dataAccessPolicy;
    }
}
