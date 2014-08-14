/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 6/18/14.
 */
public class UserProfile {
    @XSSFreeString
    @ApiModelProperty(position = 1, required = false, value = "The headline of the user resource.")
    private String headline;

    @XSSFreeString
    @ApiModelProperty(position = 2, required = false, value = "The summary of the user resource.")
    private String summary;

    @XSSFreeString
    @ApiModelProperty(position = 3, required = false, value = "The webpage of the user resource.")
    private String webpage;

    @ApiModelProperty(position = 4, required = false, value = "The avatar url of the user resource.")
    private UserAvatar avatar;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getWebpage() {
        return webpage;
    }

    public void setWebpage(String webpage) {
        this.webpage = webpage;
    }

    public UserAvatar getAvatar() {
        return avatar;
    }

    public void setAvatar(UserAvatar avatar) {
        this.avatar = avatar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserProfile that = (UserProfile) o;

        if (avatar != null ? !avatar.equals(that.avatar) : that.avatar != null) return false;
        if (headline != null ? !headline.equals(that.headline) : that.headline != null) return false;
        if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
        if (webpage != null ? !webpage.equals(that.webpage) : that.webpage != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = headline != null ? headline.hashCode() : 0;
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (webpage != null ? webpage.hashCode() : 0);
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
        return result;
    }
}
