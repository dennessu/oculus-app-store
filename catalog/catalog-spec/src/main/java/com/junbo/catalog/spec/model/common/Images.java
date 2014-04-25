/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Video.
 */
public class Images {
    @ApiModelProperty(position = 1, required = true, value = "Thumb image")
    private Image thumb;
    @ApiModelProperty(position = 1, required = true, value = "Main image")
    private Image main;
    @ApiModelProperty(position = 1, required = true, value = "Other images")
    private List<Image> general;

    public Image getThumb() {
        return thumb;
    }

    public void setThumb(Image thumb) {
        this.thumb = thumb;
    }

    public Image getMain() {
        return main;
    }

    public void setMain(Image main) {
        this.main = main;
    }

    public List<Image> getGeneral() {
        return general;
    }

    public void setGeneral(List<Image> general) {
        this.general = general;
    }
}
