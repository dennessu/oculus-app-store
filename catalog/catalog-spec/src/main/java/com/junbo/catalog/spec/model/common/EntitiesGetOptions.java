/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.junbo.catalog.common.util.Constants;
import com.junbo.common.id.Id;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Entity list get options.
 */
public abstract class EntitiesGetOptions {
    // paging params
    @QueryParam("start")
    private Integer start;

    @QueryParam("size")
    private Integer size;

    // this parameter only applies for 'Released' entities
    @QueryParam("timestamp")
    private Long timestamp;

    // defaults to get 'Released' entities
    @QueryParam("status")
    private String status;

    public EntitiesGetOptions ensurePagingValid() {
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public abstract <T extends Id> List<T> getEntityIds();
}
