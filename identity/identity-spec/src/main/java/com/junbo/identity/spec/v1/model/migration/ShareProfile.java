/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.migration;

import com.junbo.common.jackson.annotation.XSSFreeRichText;
import com.junbo.common.jackson.annotation.XSSFreeString;

/**
 * Created by liangfu on 6/6/14.
 */
public class ShareProfile {

    @XSSFreeString
    private String headline;

    @XSSFreeRichText
    private String summary;

    @XSSFreeString
    private String url;

    private ShareProfileAvatar avatar;

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

    public ShareProfileAvatar getAvatar() {
        return avatar;
    }

    public void setAvatar(ShareProfileAvatar avatar) {
        this.avatar = avatar;
    }
}
