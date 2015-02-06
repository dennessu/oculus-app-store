/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.option.list;

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

    @QueryParam("userAttributeDefinitionId")
    private UserAttributeDefinitionId userAttributeDefinitionId;

    @QueryParam("isActive")
    private Boolean isActive;

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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
