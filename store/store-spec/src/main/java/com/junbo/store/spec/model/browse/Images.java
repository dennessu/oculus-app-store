/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.store.spec.model.browse.document.Image;

import java.util.List;

/**
 * The Images class.
 */
public class Images {

    private Image main;

    private Image halfMain;

    private Image thumbnail;

    private Image halfThumbnail;

    private Image background;

    private Image featured;

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
