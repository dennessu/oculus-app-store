/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.junbo.catalog.common.util.Constants;

import javax.ws.rs.QueryParam;

/**
 * Created by baojing on 3/12/14.
 */
public class PageableGetOptions {
    // paging params
    @QueryParam("start")
    private Integer start;

    @QueryParam("size")
    private Integer size;

    public PageableGetOptions ensurePagingValid() {
        if (start == null || size == null) {
            start = Constants.DEFAULT_PAGING_START;
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
