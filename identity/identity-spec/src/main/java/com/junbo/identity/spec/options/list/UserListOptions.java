/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.options.list;

import groovy.transform.CompileStatic;

import javax.ws.rs.QueryParam;

/**
 * Created by liangfu on 3/25/14.
 */
@CompileStatic
public class UserListOptions extends PagingGetOptions {
    @QueryParam("username")
    private String username;

    @QueryParam("properties")
    private String properties;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }
}
