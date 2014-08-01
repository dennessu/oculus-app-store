/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/26/14.
 */
public class UserName {
    @XSSFreeString
    @ApiModelProperty(position = 2, required = false, value = "[Nullable] Null or the given name.")
    private String givenName;

    @XSSFreeString
    @ApiModelProperty(position = 3, required = false, value = "[Nullable] Null or the middle name or perhaps the first letter of the middle name (middle initial).")
    private String middleName;

    @XSSFreeString
    @ApiModelProperty(position = 4, required = false, value = "[Nullable] Null or the family name.")
    private String familyName;

    @XSSFreeString
    @ApiModelProperty(position = 5, required = false, value = "[Nullable] Null or the preferred-name / nick-name.")
    private String nickName;

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserName userName = (UserName) o;

        if (familyName != null ? !familyName.equals(userName.familyName) : userName.familyName != null) return false;
        if (givenName != null ? !givenName.equals(userName.givenName) : userName.givenName != null) return false;
        if (middleName != null ? !middleName.equals(userName.middleName) : userName.middleName != null) return false;
        if (nickName != null ? !nickName.equals(userName.nickName) : userName.nickName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = givenName != null ? givenName.hashCode() : 0;
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
        result = 31 * result + (nickName != null ? nickName.hashCode() : 0);
        return result;
    }
}
