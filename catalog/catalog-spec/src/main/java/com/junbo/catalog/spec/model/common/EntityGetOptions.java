/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import javax.ws.rs.QueryParam;

/**
 * Entity get options.
 */
public class EntityGetOptions {
    @QueryParam("timestamp")
    private Long timestamp;

    @QueryParam("status")
    private String status;

    public static EntityGetOptions getDefault() {
        EntityGetOptions options = new EntityGetOptions();
        options.setStatus(Status.RELEASED);

        return options;
    }

    public static EntityGetOptions getDraft() {
        EntityGetOptions options = new EntityGetOptions();
        options.setStatus(Status.DRAFT);

        return options;
    }

    public static EntityGetOptions getReviewing() {
        EntityGetOptions options = new EntityGetOptions();
        options.setStatus(Status.PENDING_REVIEW);

        return options;
    }

    public static EntityGetOptions getHistory(Long timestamp) {
        EntityGetOptions options = new EntityGetOptions();
        options.setTimestamp(timestamp);

        return options;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
