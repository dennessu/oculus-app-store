/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.json.PropertyAssignedAware;
import com.junbo.common.json.PropertyAssignedAwareSupport;

import java.util.Date;
import java.util.Map;

/**
 * The base class for all resource using propertyAssignedAware with system properties.
 */
public abstract class PropertyAssignedAwareResourceMeta<K> extends ResourceMeta<K> implements PropertyAssignedAware {

    protected final PropertyAssignedAwareSupport support = new PropertyAssignedAwareSupport();

    @Override
    public void setRev(String rev) {
        super.setRev(rev);
        support.setPropertyAssigned("rev");
    }

    @Override
    public void setCreatedTime(Date createdTime) {
        super.setCreatedTime(createdTime);
        support.setPropertyAssigned("createdTime");
    }

    @Override
    public void setUpdatedTime(Date updatedTime) {
        super.setUpdatedTime(updatedTime);
        support.setPropertyAssigned("updatedTime");
    }

    @Override
    public void setAdminInfo(AdminInfo adminInfo) {
        super.setAdminInfo(adminInfo);
        support.setPropertyAssigned("adminInfo");
    }

    @Override
    public void setCreatedBy(Long createdBy) {
        super.setCreatedBy(createdBy);
        support.setPropertyAssigned("createdBy");
    }

    @Override
    public void setUpdatedBy(Long updatedBy) {
        super.setUpdatedBy(updatedBy);
        support.setPropertyAssigned("updatedBy");
    }

    @Override
    public void setCreatedByClient(String createdByClient) {
        super.setCreatedByClient(createdByClient);
        support.setPropertyAssigned("createdByClient");
    }

    @Override
    public void setUpdatedByClient(String updatedByClient) {
        super.setUpdatedByClient(updatedByClient);
        support.setPropertyAssigned("updatedByClient");
    }

    @Override
    public void setFutureExpansion(Map<String, JsonNode> futureExpansion) {
        super.setFutureExpansion(futureExpansion);
        support.setPropertyAssigned("futureExpansion");
    }

    @Override
    public boolean isPropertyAssigned(String propertyName) {
        return support.isPropertyAssigned(propertyName);
    }
}
