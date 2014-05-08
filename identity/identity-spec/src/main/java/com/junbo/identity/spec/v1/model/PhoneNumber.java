/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import java.util.Date;

/**
 * Created by liangfu on 4/26/14.
 */
public class PhoneNumber {
    private String value;

    private Boolean isValidated;

    private Date validateTime;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(Boolean isValidated) {
        this.isValidated = isValidated;
    }

    public Date getValidateTime() {
        return validateTime;
    }

    public void setValidateTime(Date validateTime) {
        this.validateTime = validateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhoneNumber that = (PhoneNumber) o;

        if (isValidated != null ? !isValidated.equals(that.isValidated) : that.isValidated != null) return false;
        if (validateTime != null ? !validateTime.equals(that.validateTime) : that.validateTime != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (isValidated != null ? isValidated.hashCode() : 0);
        result = 31 * result + (validateTime != null ? validateTime.hashCode() : 0);
        return result;
    }
}
