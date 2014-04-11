/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.options.list;

import com.junbo.common.id.UserPiiId;
import groovy.transform.CompileStatic;

import javax.ws.rs.QueryParam;

/**
 * Created by liangfu on 3/14/14.
 */
@CompileStatic
public class UserEmailListOptions extends PagingGetOptions {

    @QueryParam("value")
    private String value;

    @QueryParam("type")
    private String type;

    @QueryParam("userPiiId")
    private UserPiiId userPiiId;

    @QueryParam("properties")
    private String properties;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserPiiId getUserPiiId() {
        return userPiiId;
    }

    public void setUserPiiId(UserPiiId userPiiId) {
        this.userPiiId = userPiiId;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }
}
