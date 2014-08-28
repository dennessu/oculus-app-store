/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.store.spec.model.browse.document.Image;

/**
 * The ImageGalleryEntry class.
 */
public class ImageGalleryEntry {

    private Image thumbnail;

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
