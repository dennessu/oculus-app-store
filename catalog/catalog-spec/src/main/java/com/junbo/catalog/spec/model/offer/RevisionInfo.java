/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

/**
 * revision info.
 */
public class RevisionInfo {
    private Long revisionId;
    private Long startTime;
    private Long endTime;
    private Long approvedTime;

    public Long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(Long revisionId) {
        this.revisionId = revisionId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getApprovedTime() {
        return approvedTime;
    }

    public void setApprovedTime(Long approvedTime) {
        this.approvedTime = approvedTime;
    }
}
