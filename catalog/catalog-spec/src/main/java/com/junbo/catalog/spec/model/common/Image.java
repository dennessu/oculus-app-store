/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Image.
 */
public class Image {
    @ApiModelProperty(position = 1, required = true, value = "The ID of the image")
    private String id;
    @ApiModelProperty(position = 2, required = true, value = "The location of the image")
    private String href;
    @ApiModelProperty(position = 3, required = true, value = "Size in bytes")
    private Long size;
    @JsonProperty("x")
    @ApiModelProperty(position = 4, required = false, value = "The width of the image")
    private Integer width;
    @JsonProperty("y")
    @ApiModelProperty(position = 5, required = false, value = "The height of the image")
    private Integer height;

    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}
