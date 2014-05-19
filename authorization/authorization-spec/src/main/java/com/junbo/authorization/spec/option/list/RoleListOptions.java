/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.spec.option.list;

import javax.ws.rs.QueryParam;

/**
 * RoleListOptions.
 */
public class RoleListOptions extends PagingListOptions {
    @QueryParam("name")
    private String name;

    @QueryParam("targetType")
    private String targetType;

    @QueryParam("filterType")
    private String filterType;

    @QueryParam("filterLink")
    private String filterLink;

    private String filterLinkIdType;

    private Long filterLinkId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getFilterLink() {
        return filterLink;
    }

    public void setFilterLink(String filterLink) {
        this.filterLink = filterLink;
    }

    public String getFilterLinkIdType() {
        return filterLinkIdType;
    }

    public void setFilterLinkIdType(String filterLinkIdType) {
        this.filterLinkIdType = filterLinkIdType;
    }

    public Long getFilterLinkId() {
        return filterLinkId;
    }

    public void setFilterLinkId(Long filterLinkId) {
        this.filterLinkId = filterLinkId;
    }
}
