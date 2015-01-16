/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.model;

import com.junbo.common.id.UserId;

/**
 * QueryParam.
 */
public class QueryParam {
    @javax.ws.rs.QueryParam("source")
    private String source;

    @javax.ws.rs.QueryParam("action")
    private String action;

    @javax.ws.rs.QueryParam("locale")
    private String locale;

    @javax.ws.rs.QueryParam("page")
    private Integer page;

    @javax.ws.rs.QueryParam("size")
    private Integer size;

    @javax.ws.rs.QueryParam("user")
    private UserId userId;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
