/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Revision notes.
 */
public class RevisionNotes {
    @XSSFreeString
    @ApiModelProperty(position = 1, required = true, value = "Short notes")
    private String shortNotes;
    @XSSFreeString
    @ApiModelProperty(position = 2, required = true, value = "Long notes")
    private String longNotes;

    public String getShortNotes() {
        return shortNotes;
    }

    public void setShortNotes(String shortNotes) {
        this.shortNotes = shortNotes;
    }

    public String getLongNotes() {
        return longNotes;
    }

    public void setLongNotes(String longNotes) {
        this.longNotes = longNotes;
    }
}
