/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Gallery entry.
 */
public class ImageGalleryEntry {
    @ApiModelProperty(position = 1, required = true, value = "Thumbnail image")
    private Image thumbnail;
    @ApiModelProperty(position = 2, required = true, value = "Full image")
    private Image full;

    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Image getFull() {
        return full;
    }

    public void setFull(Image full) {
        this.full = full;
    }
}
