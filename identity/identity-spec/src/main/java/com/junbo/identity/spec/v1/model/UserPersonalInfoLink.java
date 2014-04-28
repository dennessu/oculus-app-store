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

    @ApiModelProperty(position = 1, required = true, value = "Whether this link is default.")
    private Boolean isdefault;

    @ApiModelProperty(position = 3, required = true, value = "Resource Link to the userPersonalInfo resource.")
    private UserPersonalInfoId value;

    @JsonIgnore
    private UserId userId;


    public Boolean getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(Boolean isdefault) {
        this.isdefault = isdefault;
    }

    public UserPersonalInfoId getValue() {
        return value;
    }

    public void setValue(UserPersonalInfoId value) {
        this.value = value;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
