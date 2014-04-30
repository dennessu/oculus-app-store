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
    // paging params
    @QueryParam("start")
    private Integer start;

    @QueryParam("size")
    private Integer size;

    public PageableGetOptions ensurePagingValid() {
        if (start == null || start < 0) {
            start = Constants.DEFAULT_PAGING_START;
        }
        if (size == null || size < 0) {
            size = Constants.DEFAULT_PAGING_SIZE;
        }

        return this;
    }

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
}
