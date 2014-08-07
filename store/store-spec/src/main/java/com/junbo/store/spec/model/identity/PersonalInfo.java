/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.identity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.UserPersonalInfoId;

/**
 * The PersonalInfo class.
 */
public class PersonalInfo {

    private UserPersonalInfoId self;

    private JsonNode value;

    @JsonIgnore
    private String type;

    private Boolean isDefault;

    private String lable;

    public UserPersonalInfoId getSelf() {
        return self;
    }

    public void setSelf(UserPersonalInfoId self) {
        this.self = self;
    }

    public JsonNode getValue() {
        return value;
    }

    public void setValue(JsonNode value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
