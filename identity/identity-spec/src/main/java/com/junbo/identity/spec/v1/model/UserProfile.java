/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.junbo.common.model.Link;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 6/18/14.
 */
public class UserProfile {
    @ApiModelProperty(position = 1, required = false, value = "The headline of the user resource.")
    private String headline;

    @ApiModelProperty(position = 2, required = false, value = "The summary of the user resource.")
    private String summary;

    @ApiModelProperty(position = 3, required = false, value = "The url of the user resource.")
    private String url;

    @ApiModelProperty(position = 4, required = false, value = "The avatar link of the user resource.")
    private Link avatar;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Link getAvatar() {
        return avatar;
    }

    public void setAvatar(Link avatar) {
        this.avatar = avatar;
    }
}
