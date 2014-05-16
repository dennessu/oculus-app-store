/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.spec.model;

import com.junbo.common.json.PropertyAssignedAware;
import com.junbo.common.json.PropertyAssignedAwareSupport;
import com.junbo.common.model.Link;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Role Target.
 */
public class RoleTarget implements PropertyAssignedAware {

    protected final PropertyAssignedAwareSupport support = new PropertyAssignedAwareSupport();

    @ApiModelProperty(position = 1, required = true, value = "The target type.")
    private String targetType;

    @ApiModelProperty(position = 2, required = true, value = "The filter type. Could be self, user, etc.")
    private String filterType;

    @ApiModelProperty(position = 3, required = true, value = "The filter link. Needs to be an item resource.")
    private Link filterLink;

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
        support.setPropertyAssigned("targetType");
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
        support.setPropertyAssigned("filterType");
    }

    public Link getFilterLink() {
        return filterLink;
    }

    public void setFilterLink(Link filterLink) {
        this.filterLink = filterLink;
        support.setPropertyAssigned("filterLink");
    }

    @Override
    public boolean isPropertyAssigned(String propertyName) {
        return support.isPropertyAssigned(propertyName);
    }
}
