/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.casey;

import javax.ws.rs.QueryParam;

/**
 * ReviewSearchParams.
 */
public class ReviewSearchParams extends CaseySearchParams {
    @QueryParam("resourceType")
    private String resourceType;

    @QueryParam("resourceId")
    private String resourceId;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
