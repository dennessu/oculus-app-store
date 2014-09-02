/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * Gallery entry.
 */
public class ImageGalleryEntry {
    @ApiModelProperty(position = 1, required = true, value = "Thumbnail image")
    private Map<String, Image> thumbnail;
    @ApiModelProperty(position = 2, required = true, value = "Full image")
    private Map<String, Image> full;

    public Map<String, Image> getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Map<String, Image> thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Map<String, Image> getFull() {
        return full;
    }

    public void setFull(Map<String, Image> full) {
        this.full = full;
    }
}
