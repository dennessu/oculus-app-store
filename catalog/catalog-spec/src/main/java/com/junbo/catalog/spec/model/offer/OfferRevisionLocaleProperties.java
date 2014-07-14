/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.junbo.catalog.spec.model.common.Images;
import com.junbo.catalog.spec.model.common.Video;
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * Locale properties for offer revision.
 */
public class OfferRevisionLocaleProperties {
    @ApiModelProperty(position = 1, required = true, value = "offer revision name")
    private String name;
    @ApiModelProperty(position = 3, required = true, value = "long description")
    private String longDescription;
    @ApiModelProperty(position = 4, required = true, value = "short description")
    private String shortDescription;

    @ApiModelProperty(position = 20, required = true, value = "Images to describe the offer revision resource")
    private Images images;
    @ApiModelProperty(position = 21, required = true, value = "Videos to describe the offer revision resource")
    private List<Video> videos;

    @ApiModelProperty(position = 30, required = true, value = "Locales properties for items")
    private Map<String, ItemRevisionLocaleProperties> items;

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public Map<String, ItemRevisionLocaleProperties> getItems() {
        return items;
    }

    public void setItems(Map<String, ItemRevisionLocaleProperties> items) {
        this.items = items;
    }
}
