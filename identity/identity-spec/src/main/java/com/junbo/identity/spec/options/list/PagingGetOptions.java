/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.options.list;

import javax.ws.rs.QueryParam;

/**
 * Created by liangfu on 3/13/14.
 */
public class PagingGetOptions {
    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @QueryParam("limit")
    private Integer limit;
    @QueryParam("offset")
    private Integer offset;
}
