/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.model.Link;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Role Target.
 */
public class RoleTarget {

    @ApiModelProperty(position = 1, required = true, value = "The target type, only support organizations.")
    private String targetType;

    @ApiModelProperty(position = 2, required = true, value = "The filter type. only support SINGLEINSTANCEFILTER.")
    private String filterType;

    @ApiModelProperty(position = 3, required = true, value = "The filter link. Needs to be an organization resource.")
    private Link filterLink;

    @JsonIgnore
    private String filterLinkIdType;

    @JsonIgnore
    private String filterLinkId;

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public Link getFilterLink() {
        return filterLink;
    }

    public void setFilterLink(Link filterLink) {
        this.filterLink = filterLink;
    }

    public String getFilterLinkIdType() {
        return filterLinkIdType;
    }

    public void setFilterLinkIdType(String filterLinkIdType) {
        this.filterLinkIdType = filterLinkIdType;
    }

    public String getFilterLinkId() {
        return filterLinkId;
    }

    public void setFilterLinkId(String filterLinkId) {
        this.filterLinkId = filterLinkId;
    }
}
