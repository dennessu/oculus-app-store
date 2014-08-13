/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 6/19/14.
 */
public class UserAvatar {

    @XSSFreeString
    @ApiModelProperty(position = 1, required = false, value = "The avatar url.")
    private String href;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAvatar that = (UserAvatar) o;

        if (href != null ? !href.equals(that.href) : that.href != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return href != null ? href.hashCode() : 0;
    }
}
