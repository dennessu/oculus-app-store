/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 6/18/14.
 */
public class UserProfile {
    @ApiModelProperty(position = 1, required = false, value = "The headline of the user resource.")
    private String headline;

    @ApiModelProperty(position = 2, required = false, value = "The summary of the user resource.")
    private String summary;

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
}
