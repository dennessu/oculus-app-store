/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.junbo.catalog.common.util.Constants;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Entity list get options.
 */
public class EntitiesGetOptions {
    private Integer start;
    private Integer size;
    private List<Long> entityIds;
    private Long timestamp;
    private String status;

    public EntitiesGetOptions ensurePagingValid() {
        if (start == null || size == null) {
            start = Constants.DEFAULT_PAGING_START;
            size = Constants.DEFAULT_PAGING_SIZE;
        }

        return this;
    }
    // paging params
    @QueryParam("start")
    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    @QueryParam("size")
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    // if entityIds is specified, paging params will be ignored.
    @QueryParam("id")
    public List<Long> getEntityIds() {
        return entityIds;
    }

    public void setEntityIds(List<Long> entityIds) {
        this.entityIds = entityIds;
    }

    // this parameter only applies for 'Released' entities
    @QueryParam("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    // defaults to get 'Released' entities
    @QueryParam("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
