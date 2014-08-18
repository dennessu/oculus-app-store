/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.spec.model;

import javax.ws.rs.QueryParam;

/**
 * PageMetaData Model.
 */
public class PageMetadata {
    /**
     * @deprecated Start is not used any more because start is only used for sql only and no longer available for cloudant.
     */
    @Deprecated
    private Integer start;

    @QueryParam("count")
    private Integer count;

    @QueryParam("bookmark")
    private String bookmark;

    /**
     * @deprecated Start is not used any more because start is only used for sql only and no longer available for cloudant.
     */
    @Deprecated
    public Integer getStart() {
        return start;
    }

    /**
     * @deprecated Start is not used any more because start is only used for sql only and no longer available for cloudant.
     */
    @Deprecated
    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getBookmark() {
        return bookmark;
    }

    public void setBookmark(String bookmark) {
        this.bookmark = bookmark;
    }
}
