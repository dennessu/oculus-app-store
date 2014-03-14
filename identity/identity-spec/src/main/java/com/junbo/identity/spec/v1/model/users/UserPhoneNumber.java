/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.users;

import com.junbo.common.id.UserPhoneNumberId;
import com.junbo.common.util.Identifiable;

/**
 * Created by kg on 3/12/14.
 */
public class UserPhoneNumber extends ResourceMeta implements Identifiable<UserPhoneNumberId> {

    private UserPhoneNumberId id;

    private String type;

    private String value;

    private Boolean primary;

    private Boolean verified;

    public UserPhoneNumberId getId() {
        return id;
    }

    public void setId(UserPhoneNumberId id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
