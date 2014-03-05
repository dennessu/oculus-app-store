/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.password;

/**
 * Created by liangfu on 2/24/14.
 */
public class PasswordRuleDetail {
    private Integer minPasswordLength;
    private Integer maxPasswordLength;

    private Integer minDigitalLength;
    private Integer maxDigitalLength;

    private Integer minUpperAlphaLength;
    private Integer maxUpperAlphaLength;

    private Integer minLowerAlphaLength;
    private Integer maxLowerAlphaLength;

    private Integer minSpecialCharacterLength;
    private Integer maxSpecialCharacterLength;

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PasswordRuleDetail)) {
            return false;
        }

        PasswordRuleDetail detail = (PasswordRuleDetail)obj;

        if(this.minPasswordLength == detail.minPasswordLength
        && this.maxPasswordLength == detail.maxPasswordLength
        && this.minDigitalLength == detail.minDigitalLength
        && this.maxDigitalLength == detail.maxDigitalLength
        && this.minUpperAlphaLength == detail.minUpperAlphaLength
        && this.maxUpperAlphaLength == detail.maxUpperAlphaLength
        && this.minLowerAlphaLength == detail.minLowerAlphaLength
        && this.maxLowerAlphaLength == detail.maxLowerAlphaLength
        && this.minSpecialCharacterLength == detail.minSpecialCharacterLength
        && this.maxSpecialCharacterLength == detail.maxSpecialCharacterLength) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public Integer getMinPasswordLength() {
        return minPasswordLength;
    }

    public void setMinPasswordLength(Integer minPasswordLength) {
        this.minPasswordLength = minPasswordLength;
    }

    public Integer getMaxPasswordLength() {
        return maxPasswordLength;
    }

    public void setMaxPasswordLength(Integer maxPasswordLength) {
        this.maxPasswordLength = maxPasswordLength;
    }

    public Integer getMinDigitalLength() {
        return minDigitalLength;
    }

    public void setMinDigitalLength(Integer minDigitalLength) {
        this.minDigitalLength = minDigitalLength;
    }

    public Integer getMaxDigitalLength() {
        return maxDigitalLength;
    }

    public void setMaxDigitalLength(Integer maxDigitalLength) {
        this.maxDigitalLength = maxDigitalLength;
    }

    public Integer getMinUpperAlphaLength() {
        return minUpperAlphaLength;
    }

    public void setMinUpperAlphaLength(Integer minUpperAlphaLength) {
        this.minUpperAlphaLength = minUpperAlphaLength;
    }

    public Integer getMaxUpperAlphaLength() {
        return maxUpperAlphaLength;
    }

    public void setMaxUpperAlphaLength(Integer maxUpperAlphaLength) {
        this.maxUpperAlphaLength = maxUpperAlphaLength;
    }

    public Integer getMinLowerAlphaLength() {
        return minLowerAlphaLength;
    }

    public void setMinLowerAlphaLength(Integer minLowerAlphaLength) {
        this.minLowerAlphaLength = minLowerAlphaLength;
    }

    public Integer getMaxLowerAlphaLength() {
        return maxLowerAlphaLength;
    }

    public void setMaxLowerAlphaLength(Integer maxLowerAlphaLength) {
        this.maxLowerAlphaLength = maxLowerAlphaLength;
    }

    public Integer getMinSpecialCharacterLength() {
        return minSpecialCharacterLength;
    }

    public void setMinSpecialCharacterLength(Integer minSpecialCharacterLength) {
        this.minSpecialCharacterLength = minSpecialCharacterLength;
    }

    public Integer getMaxSpecialCharacterLength() {
        return maxSpecialCharacterLength;
    }

    public void setMaxSpecialCharacterLength(Integer maxSpecialCharacterLength) {
        this.maxSpecialCharacterLength = maxSpecialCharacterLength;
    }
}
