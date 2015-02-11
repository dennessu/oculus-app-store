/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.option.list;

import com.junbo.common.id.OrganizationId;
import com.junbo.common.id.UserAttributeDefinitionId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.options.list.PagingGetOptions;

import javax.ws.rs.QueryParam;

/**
 * Created by xiali_000 on 2014/12/19.
 */
public class UserAttributeListOptions extends PagingGetOptions {
    @QueryParam("properties")
    private String properties;

    @QueryParam("userId")
    private UserId userId;

    @QueryParam("type")
    private String type;

    @QueryParam("organizationId")
    private OrganizationId organizationId;

    @QueryParam("definitionId")
    private UserAttributeDefinitionId userAttributeDefinitionId;

    @QueryParam("activeOnly")
    private Boolean activeOnly;

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public UserAttributeDefinitionId getUserAttributeDefinitionId() {
        return userAttributeDefinitionId;
    }

    public void setUserAttributeDefinitionId(UserAttributeDefinitionId userAttributeDefinitionId) {
        this.userAttributeDefinitionId = userAttributeDefinitionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(OrganizationId organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean getActiveOnly() {
        return activeOnly;
    }

    public void setActiveOnly(Boolean activeOnly) {
        this.activeOnly = activeOnly;
    }
}
