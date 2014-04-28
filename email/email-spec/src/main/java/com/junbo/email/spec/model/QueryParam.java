/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.model;

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
}
