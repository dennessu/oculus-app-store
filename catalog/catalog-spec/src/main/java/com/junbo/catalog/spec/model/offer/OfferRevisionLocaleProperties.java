/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Locale properties for offer revision.
 */
public class OfferRevisionLocaleProperties {
    @ApiModelProperty(position = 1, required = true, value = "offer revision name")
    String name;
    @ApiModelProperty(position = 2, required = true, value = "revision notes")
    String revisionNotes;
    @ApiModelProperty(position = 3, required = true, value = "long description")
    String longDescription;
    @ApiModelProperty(position = 4, required = true, value = "short description")
    String shortDescription;

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRevisionNotes() {
        return revisionNotes;
    }

    public void setRevisionNotes(String revisionNotes) {
        this.revisionNotes = revisionNotes;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
}
