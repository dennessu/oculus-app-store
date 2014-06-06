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
public class UserGender {
    @ApiModelProperty(position = 1, required = true, value = "The user's gender. It must be in [MALE, FEMALE]")
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

        UserGender that = (UserGender) o;

        if (info != null ? !info.equals(that.info) : that.info != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return info != null ? info.hashCode() : 0;
    }
}
