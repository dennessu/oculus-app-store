/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.junbo.catalog.common.util.Constants;

import javax.ws.rs.QueryParam;

/**
 * PageableGetOptions.
 */
public class PageableGetOptions {
    @QueryParam("cursor")
    private String cursor;

    @QueryParam("count")
    private Integer size;

    private String nextCursor;
    private Long total;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }

    public Integer getValidSize() {
        if (size == null || size < 0) {
            return Constants.DEFAULT_PAGING_SIZE;
        }
        return size;
    }

    public Integer getValidStart() {
        try{
            Integer start = Integer.valueOf(cursor);
            if (start == null || start < 0) {
                return Constants.DEFAULT_PAGING_START;
            }
            return start;
        } catch (NumberFormatException e) {
            return Constants.DEFAULT_PAGING_START;
        }

    }

    public Integer nextStart() {
        return getValidStart() + getValidSize();
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
