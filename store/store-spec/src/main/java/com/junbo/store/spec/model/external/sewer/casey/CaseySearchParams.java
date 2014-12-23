/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sewer.casey;

import javax.ws.rs.QueryParam;

/**
 * CaseySearchParams.
 */
public abstract class CaseySearchParams {
    @QueryParam("cursor")
    protected String cursor;

    @QueryParam("count")
    protected Integer count;

    @QueryParam("locale")
    protected String locale;

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

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
