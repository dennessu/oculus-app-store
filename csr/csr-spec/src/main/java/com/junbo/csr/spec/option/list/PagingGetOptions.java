/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.spec.option.list;

import javax.ws.rs.QueryParam;

/**
 * Created by haomin on 14-7-4.
 */
public class PagingGetOptions {
    @QueryParam("count")
    private Integer limit;
    @QueryParam("cursor")
    private Integer offset;
    @QueryParam("total")
    private Boolean total;

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

    public Boolean getTotal() {
        return total;
    }

    public void setTotal(Boolean total) {
        this.total = total;
    }
}
