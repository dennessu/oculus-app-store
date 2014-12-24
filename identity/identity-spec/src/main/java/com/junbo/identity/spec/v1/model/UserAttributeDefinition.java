/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserAttributeDefinitionId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

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
}
