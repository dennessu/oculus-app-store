/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Images.
 */
public class Images {
    @ApiModelProperty(position = 1, required = true, value = "Main image")
    private Image main;

    @ApiModelProperty(position = 2, required = true, value = "Half-main image")
    private Image halfMain;

    @ApiModelProperty(position = 3, required = true, value = "Thumbnail image")
    private Image thumbnail;

    @ApiModelProperty(position = 4, required = true, value = "Half-thumbnail image")
    private Image halfThumbnail;

    @ApiModelProperty(position = 5, required = true, value = "Background image")
    private Image background;

    @ApiModelProperty(position = 6, required = true, value = "Featured image")
    private Image featured;

    @ApiModelProperty(position = 7, required = true, value = "Gallery images")
    private List<ImageGalleryEntry> gallery;

    public Image getMain() {
        return main;
    }

    public void setMain(Image main) {
        this.main = main;
    }

    public Image getHalfMain() {
        return halfMain;
    }

    public void setHalfMain(Image halfMain) {
        this.halfMain = halfMain;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Image getHalfThumbnail() {
        return halfThumbnail;
    }

    public void setHalfThumbnail(Image halfThumbnail) {
        this.halfThumbnail = halfThumbnail;
    }

    public Image getBackground() {
        return background;
    }

    public void setBackground(Image background) {
        this.background = background;
    }

    public Image getFeatured() {
        return featured;
    }

    public void setFeatured(Image featured) {
        this.featured = featured;
    }

    public List<ImageGalleryEntry> getGallery() {
        return gallery;
    }

    public void setGallery(List<ImageGalleryEntry> gallery) {
        this.gallery = gallery;
    }
}
