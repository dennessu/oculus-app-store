/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.UserPiiId;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by kg on 3/10/14.
 */
public class UserEmail {

    @ApiModelProperty(position = 1, required = true, value = "User email.")
    private String value;

    @ApiModelProperty(position = 2, required = true, value = "Whether user email is verified.")
    private Boolean verified;

    @JsonIgnore
    private UserPiiId userPiiId;

    @JsonIgnore
    private String type;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public UserPiiId getUserPiiId() {
        return userPiiId;
    }

    public void setUserPiiId(UserPiiId userPiiId) {
        this.userPiiId = userPiiId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
