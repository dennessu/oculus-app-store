/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.userlog;

import com.junbo.common.id.UserId;
import groovy.transform.CompileStatic;

import javax.ws.rs.QueryParam;

/**
 * UserLog list options.
 */
@CompileStatic
public class UserLogListOptions {
    @QueryParam("userId")
    private UserId userId;
    @QueryParam("clientId")
    private String clientId;
    @QueryParam("apiName")
    private String apiName;
    @QueryParam("httpMethod")
    private String httpMethod;
    @QueryParam("sequenceId")
    private String sequenceId;
    @QueryParam("isDescending")
    private Boolean isDescending;
    @QueryParam("isOK")
    private Boolean isOK;

    @QueryParam("cursor")
    private String cursor;
    @QueryParam("count")
    private Integer count;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public Boolean getIsDescending() {
        return isDescending;
    }

    public void setIsDescending(Boolean isDescending) {
        this.isDescending = isDescending;
    }

    public Boolean getIsOK() {
        return isOK;
    }

    public void setIsOK(Boolean isOK) {
        this.isOK = isOK;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
