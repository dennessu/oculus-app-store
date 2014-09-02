/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.store.spec.model.browse.document.Image;

import java.util.List;
import java.util.Map;

/**
 * The Images class.
 */
public class Images {

    private Map<String, Image> main;

    private Map<String, Image> thumbnail;

    private Map<String, Image> background;

    private Map<String, Image> featured;

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
