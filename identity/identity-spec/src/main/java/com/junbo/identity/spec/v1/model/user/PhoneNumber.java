/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.PhoneNumberId;
import com.junbo.common.id.UserId;

/**
 * Created by kg on 3/12/14.
 */
public class PhoneNumber {

    @JsonProperty("user")
    private UserId userId;

    @JsonProperty("self")
    private PhoneNumberId id;

    private String value;

    private String display;  // todo:   confirm with kgu, this should be calculated according to the value.
                             // How do we calculate from value?

    private Boolean primary;

    private String type;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
