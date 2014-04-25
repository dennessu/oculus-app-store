/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/24/14.
 */
public class UserPersonalInfoLink {

    @ApiModelProperty(position = 1, required = true, value = "[Nullable]Type to validate.")
    private String type;

    @ApiModelProperty(position = 2, required = true, value = "Resource Link Label.")
    private String label;

    @ApiModelProperty(position = 3, required = true, value = "Resource Link.")
    private UserPersonalInfoId resourceLink;

    @JsonIgnore
    private UserId userId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public UserPersonalInfoId getResourceLink() {
        return resourceLink;
    }

    public void setResourceLink(UserPersonalInfoId resourceLink) {
        this.resourceLink = resourceLink;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
