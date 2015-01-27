/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.id.UserAttributeDefinitionId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by xiali_000 on 2014/12/19.
 */
public class UserAttributeDefinition extends PropertyAssignedAwareResourceMeta<UserAttributeDefinitionId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable]Link to the user Attribute Definition")
    @JsonProperty("self")
    private UserAttributeDefinitionId id;

    @ApiModelProperty(position = 2, required = true, value = "Attribute type, ['CATEGORY', 'TUTORIAL','ACHIEVEMENT']")
    private String type;

    @ApiModelProperty(position = 3, required = true, value = "Tags or description of the user Attribute, " +
            "for example \"Finished VR Home Tutorial\", \"Acheived Level 30 in Darknet\"")
    private String description;

    @ApiModelProperty(position = 4, required = false, value = "[Nullable] Link to the organization resource, to illustrate this user attribute definition is invented/introduced by this organization")
    @JsonProperty("organization")
    private OrganizationId organizationId;

    @ApiModelProperty(position = 5, required = false, value = "[Nullable] Array of Links to the item that this user attribute is associated with, for example this can be a cross base game achievement")
    private List<ItemId> items;

    public void setType(String type) {
        this.type = type;
        support.setPropertyAssigned("type");
    }

    public void setDescription(String description) {
        this.description = description;
        support.setPropertyAssigned("description");
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public UserAttributeDefinitionId getId() {
        return id;
    }

    @Override
    public void setId(UserAttributeDefinitionId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(OrganizationId organizationId) {
        this.organizationId = organizationId;
        support.setPropertyAssigned("organization");
        support.setPropertyAssigned("organizationId");
    }

    public List<ItemId> getItems() {
        return items;
    }

    public void setItems(List<ItemId> items) {
        this.items = items;
        support.setPropertyAssigned("items");
    }
}
