/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.store.spec.model.browse.document.Image;

import java.util.Map;

/**
 * The ImageGalleryEntry class.
 */
public class ImageGalleryEntry {

    private Map<String, Image> thumbnail;

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
