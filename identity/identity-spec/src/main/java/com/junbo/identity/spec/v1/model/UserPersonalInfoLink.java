/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/24/14.
 */
public class UserPersonalInfoLink {

    @ApiModelProperty(position = 1, required = true, value = "Whether this link is default.")
    private Boolean isDefault;

    @XSSFreeString
    @ApiModelProperty(position = 2, required = false, value = "Label of this link.")
    private String label;

    @ApiModelProperty(position = 3, required = true, value = "Resource Link to the userPersonalInfo resource.")
    private UserPersonalInfoId value;

    @JsonIgnore
    private UserId userId;

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPersonalInfoLink that = (UserPersonalInfoLink) o;

        if (isDefault != null ? !isDefault.equals(that.isDefault) : that.isDefault != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = isDefault != null ? isDefault.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}
