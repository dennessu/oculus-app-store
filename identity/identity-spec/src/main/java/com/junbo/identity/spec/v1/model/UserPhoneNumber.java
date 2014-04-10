/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.UserPiiId;

/**
 * Created by kg on 3/12/14.
 */
public class UserPhoneNumber {
    private String value;

    private Boolean verified;

    @JsonIgnore
    private String type;

    @JsonIgnore
    private UserPiiId userPiiId;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserPiiId getUserPiiId() {
        return userPiiId;
    }

    public void setUserPiiId(UserPiiId userPiiId) {
        this.userPiiId = userPiiId;
    }
}
