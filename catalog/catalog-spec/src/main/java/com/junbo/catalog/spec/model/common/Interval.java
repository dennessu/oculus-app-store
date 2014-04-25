/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.spec.model.common;

import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Interval.
 */
public class Interval {
    @ApiModelProperty(position = 1, required = true, value = "start time")
    private Date start;
    @ApiModelProperty(position = 2, required = true, value = "end time")
    private Date end;

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return new Date(end.getTime());
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
