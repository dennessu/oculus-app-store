/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

/**
 * Created by liangfu on 4/26/14.
 */
public class UserQQ {
    private String qq;

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserQQ userQQ = (UserQQ) o;

        if (qq != null ? !qq.equals(userQQ.qq) : userQQ.qq != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return qq != null ? qq.hashCode() : 0;
    }
}
