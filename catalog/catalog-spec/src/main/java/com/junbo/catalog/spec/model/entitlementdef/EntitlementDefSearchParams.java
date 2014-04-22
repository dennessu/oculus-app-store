/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.spec.model.entitlementdef;

import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;
import java.util.Set;

/**
 * entitlementDef search params.
 */
public class EntitlementDefSearchParams {
    @QueryParam("groups")
    private Set<String> groups;
    @QueryParam("tags")
    private Set<String> tags;
    @QueryParam("clientId")
    private String clientId;
    @QueryParam("developerId")
    private UserId developerId;
    @QueryParam("type")
    private String type;
    @QueryParam("isConsumable")
    private Boolean isConsumable;

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public UserId getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(UserId developerId) {
        this.developerId = developerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsConsumable() {
        return isConsumable;
    }

    public void setIsConsumable(Boolean isConsumable) {
        this.isConsumable = isConsumable;
    }
}
