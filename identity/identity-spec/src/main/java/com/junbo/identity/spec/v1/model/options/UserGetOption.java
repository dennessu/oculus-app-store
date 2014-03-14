/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.options;

import javax.ws.rs.QueryParam;

/**
 * Created by liangfu on 3/14/14.
 */
public class UserGetOption extends PagingGetOption {
    @QueryParam("userName")
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
