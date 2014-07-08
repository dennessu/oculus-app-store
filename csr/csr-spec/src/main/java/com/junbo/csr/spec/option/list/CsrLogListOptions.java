/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.spec.option.list;

import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;

/**
 * Created by haomin on 14-7-4.
 */
public class CsrLogListOptions extends PagingGetOptions {

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Integer getLastHours() {
        return lastHours;
    }

    public void setLastHours(Integer lastHours) {
        this.lastHours = lastHours;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @QueryParam("from")
    private String from;
    @QueryParam("to")
    private String to;
    @QueryParam("lastHours")
    private Integer lastHours;
    @QueryParam("byAgent")
    private UserId userId;
    @QueryParam("byAction")
    private String action;
}
