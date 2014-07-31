/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.option.list;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.identity.spec.options.list.PagingGetOptions;

import javax.ws.rs.QueryParam;

/**
 * Created by liangfu on 4/22/14.
 */
public class UserTFAListOptions extends PagingGetOptions {

    @QueryParam("properties")
    private String properties;

    @QueryParam("userId")
    private UserId userId;

    @QueryParam("personalInfo")
    private UserPersonalInfoId personalInfo;

    @QueryParam("type")
    private String type;

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public UserPersonalInfoId getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(UserPersonalInfoId personalInfo) {
        this.personalInfo = personalInfo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
