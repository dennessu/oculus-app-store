/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Revision notes.
 */
public class RevisionNotes {
    @ApiModelProperty(position = 1, required = true, value = "Short notes")
    private String shortNotes;
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
