/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.option;

import javax.ws.rs.QueryParam;

/**
 * PageableGetOptions.
 */
public class PageableGetOptions {
    public static final Integer DEFAULT_PAGING_START = 0;
    public static final Integer DEFAULT_PAGING_COUNT = 20;

    @QueryParam("cursor")
    private Integer cursor;

    @QueryParam("count")
    private Integer count;

    public Integer getCursor() {
        return cursor;
    }

    public void setCursor(Integer cursor) {
        this.cursor = cursor;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getValidCount() {
        if (count == null || count < 0) {
            return DEFAULT_PAGING_COUNT;
        }
        return count;
    }

    public Integer getValidCursor() {
        if (cursor == null || cursor < 0) {
            return DEFAULT_PAGING_START;
        }
        return cursor;
    }
}
