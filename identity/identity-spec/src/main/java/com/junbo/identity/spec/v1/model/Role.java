/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.RoleId;
import com.junbo.common.util.Identifiable;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Role.
 */
public class Role extends ResourceMeta implements Identifiable<RoleId> {
    @ApiModelProperty(position = 1, required = true, value = "[Nullable]The id of the role resource.")
    @JsonProperty("self")
    private RoleId id;

    @ApiModelProperty(position = 2, required = true, value = "The name of the role resource.")
    private String name;

    @ApiModelProperty(position = 3, required = true, value = "The resource Type.")
    private String resourceType;

    @ApiModelProperty(position = 4, required = true, value = "The resource Id.")
    private Long resourceId;

    @ApiModelProperty(position = 5, required = false, value = "Sub resource type.")
    private String subResourceType;

    @Override
    public RoleId getId() {
        return id;
    }

    public void setId(RoleId id) {
        this.id = id;
        support.setPropertyAssigned("self");
        support.setPropertyAssigned("id");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        support.setPropertyAssigned("name");
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
        support.setPropertyAssigned("resourceType");
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
        support.setPropertyAssigned("resourceId");
    }

    public String getSubResourceType() {
        return subResourceType;
    }

    public void setSubResourceType(String subResourceType) {
        this.subResourceType = subResourceType;
        support.setPropertyAssigned("subResourceType");
    }
}
