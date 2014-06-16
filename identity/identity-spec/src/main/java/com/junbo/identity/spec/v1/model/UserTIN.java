/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 6/16/14.
 */
public class UserTIN {

    @ApiModelProperty(position = 1, required = true, value = "The user TIN information.")
    private String info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserTIN userTIN = (UserTIN) o;

        if (info != null ? !info.equals(userTIN.info) : userTIN.info != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return info != null ? info.hashCode() : 0;
    }
}
