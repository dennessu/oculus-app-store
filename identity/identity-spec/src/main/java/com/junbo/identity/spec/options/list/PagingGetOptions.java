/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.options.list;

import javax.ws.rs.QueryParam;

/**
 * Created by liangfu on 3/13/14.
 */
public class PagingGetOptions {
    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return cursor == null ? null : Integer.parseInt(cursor);
    }

    public void setOffset(Integer offset) {
        if (offset == null) {
            cursor = null;
        } else {
            cursor = offset.toString();
        }
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    @QueryParam("count")
    private Integer limit;

    @QueryParam("cursor")
    private String cursor;
}
