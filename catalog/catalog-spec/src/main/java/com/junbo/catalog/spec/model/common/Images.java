/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * Images.
 */
public class Images {
    @ApiModelProperty(position = 1, required = true, value = "Main image")
    private Map<String, Image> main;

    @ApiModelProperty(position = 2, required = true, value = "Thumbnail image")
    private Map<String, Image> thumbnail;

    @ApiModelProperty(position = 3, required = true, value = "Background image")
    private Map<String, Image> background;

    @ApiModelProperty(position = 4, required = true, value = "Featured image")
    private Map<String, Image> featured;

    @ApiModelProperty(position = 5, required = true, value = "Gallery images")
    private List<ImageGalleryEntry> gallery;

    public Map<String, Image> getMain() {
        return main;
    }

    public void setMain(Map<String, Image> main) {
        this.main = main;
    }

    public Map<String, Image> getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Map<String, Image> thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Map<String, Image> getBackground() {
        return background;
    }

    public void setBackground(Map<String, Image> background) {
        this.background = background;
    }

    public Map<String, Image> getFeatured() {
        return featured;
    }

    public void setFeatured(Map<String, Image> featured) {
        this.featured = featured;
    }

    public List<ImageGalleryEntry> getGallery() {
        return gallery;
    }

    public void setGallery(List<ImageGalleryEntry> gallery) {
        this.gallery = gallery;
    }
}
