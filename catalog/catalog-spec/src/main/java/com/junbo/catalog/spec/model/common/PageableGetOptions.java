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
    @QueryParam("start")
    private Integer start;
    @QueryParam("size")
    private Integer size;
    @QueryParam("bookmark")
    private String bookmark;

    private String nextBookmark;

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getBookmark() {
        return bookmark;
    }

    public void setBookmark(String bookmark) {
        this.bookmark = bookmark;
    }

    public Integer getValidSize() {
        if (size == null || size < 0) {
            return Constants.DEFAULT_PAGING_SIZE;
        }
        return size;
    }

    public Integer getValidStart() {
        if (start == null || start < 0) {
            return Constants.DEFAULT_PAGING_START;
        }
        return start;
    }

    public Integer nextStart() {
        return getValidStart() + getValidSize();
    }

    public String getNextBookmark() {
        return nextBookmark;
    }

    public void setNextBookmark(String nextBookmark) {
        this.nextBookmark = nextBookmark;
    }
}
