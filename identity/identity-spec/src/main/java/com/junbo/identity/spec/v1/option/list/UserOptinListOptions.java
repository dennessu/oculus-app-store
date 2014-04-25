/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.option.list;

import com.junbo.common.id.CommunicationId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.options.list.PagingGetOptions;

import javax.ws.rs.QueryParam;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserOptinListOptions extends PagingGetOptions {
    @QueryParam("properties")
    private String properties;

    @QueryParam("userId")
    private UserId userId;

    @QueryParam("communicationId")
    private CommunicationId communicationId;

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

    public CommunicationId getCommunicationId() {
        return communicationId;
    }

    public void setCommunicationId(CommunicationId communicationId) {
        this.communicationId = communicationId;
    }
}
