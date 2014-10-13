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

    private List<Map<String, Image>> gallery;

    public Map<String, Image> getMain() {
        return main;
    }

    public void setMain(Map<String, Image> main) {
        this.main = main;
    }

    public List<Map<String, Image>> getGallery() {
        return gallery;
    }

    public void setGallery(List<Map<String, Image>> gallery) {
        this.gallery = gallery;
    }
}
