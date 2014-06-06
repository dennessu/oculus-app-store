/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/26/14.
 */
public class UserName {
    @ApiModelProperty(position = 1, required = true, value = "The full name, or at least as much of it as we know.")
    private String fullName;

    @ApiModelProperty(position = 2, required = false, value = "[Nullable] Null or the given name.")
    private String givenName;

    @ApiModelProperty(position = 3, required = false, value = "[Nullable] Null or the middle name or perhaps the first letter of the middle name (middle initial).")
    private String middleName;

    @ApiModelProperty(position = 4, required = false, value = "[Nullable] Null or the family name.")
    private String familyName;

    @ApiModelProperty(position = 5, required = false, value = "[Nullable] Null or the preferred-name / nick-name.")
    private String nickName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

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

        if (fullName != null ? !fullName.equals(userName.fullName) : userName.fullName != null) return false;
        if (middleName != null ? !middleName.equals(userName.middleName) : userName.middleName != null) return false;
        if (nickName != null ? !nickName.equals(userName.nickName) : userName.nickName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fullName != null ? fullName.hashCode() : 0;
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (nickName != null ? nickName.hashCode() : 0);
        return result;
    }
}
