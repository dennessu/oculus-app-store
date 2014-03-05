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
    // if revision is specified, status will be ignored
    @QueryParam("revision")
    private Integer revision;
    @QueryParam("revision")
    private String status;

    public static EntityGetOptions getDefault() {
        EntityGetOptions options = new EntityGetOptions();
        options.setStatus(Status.ACTIVE);

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

    public static EntityGetOptions getHistory(int revision) {
        EntityGetOptions options = new EntityGetOptions();
        options.setRevision(revision);

        return options;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }
}
