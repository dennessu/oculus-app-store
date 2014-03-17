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
public abstract class EntitiesGetOptions extends PageableGetOptions {
    // this parameter only applies for 'Released' entities
    @QueryParam("timestamp")
    private Long timestamp;

    // defaults to get 'Released' entities
    @QueryParam("status")
    private String status;

    protected static  <T extends EntitiesGetOptions> T setDefaults(T options) {
        options.setStatus(Status.RELEASED);
        options.setStart(Constants.DEFAULT_PAGING_START);
        options.setSize(Constants.DEFAULT_PAGING_SIZE);

        return options;
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
