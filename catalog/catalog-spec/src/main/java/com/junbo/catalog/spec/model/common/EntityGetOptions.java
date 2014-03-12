/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

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
    private Long timestamp;
    private String status;

    public static EntityGetOptions getDefault() {
        EntityGetOptions options = new EntityGetOptions();
        options.setStatus(Status.RELEASED);

        return options;
    }

    public static EntityGetOptions getDraft() {
        EntityGetOptions options = new EntityGetOptions();
        options.setStatus(Status.DESIGN);

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

    @QueryParam("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @QueryParam("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
