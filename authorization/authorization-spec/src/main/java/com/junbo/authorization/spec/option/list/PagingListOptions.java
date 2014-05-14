/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.spec.option.list;

import javax.ws.rs.QueryParam;

/**
 * Created by Shenhua on 5/13/2014.
 */
public class PagingListOptions {

    @QueryParam("count")
    private Integer count;

    @QueryParam("cursor")
    private String cursor;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }
}
