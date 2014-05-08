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
    private Date birthday;

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDOB userDOB = (UserDOB) o;

        if (birthday != null ? !birthday.equals(userDOB.birthday) : userDOB.birthday != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return birthday != null ? birthday.hashCode() : 0;
    }
}
