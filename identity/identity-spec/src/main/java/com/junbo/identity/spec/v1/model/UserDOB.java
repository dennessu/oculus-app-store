/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import java.util.Date;

/**
 * Created by liangfu on 4/26/14.
 */
public class UserDOB {
    private Date info;

    public Date getInfo() {
        return info;
    }

    public void setInfo(Date info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDOB userDOB = (UserDOB) o;

        if (info != null ? !info.equals(userDOB.info) : userDOB.info != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return info != null ? info.hashCode() : 0;
    }
}
