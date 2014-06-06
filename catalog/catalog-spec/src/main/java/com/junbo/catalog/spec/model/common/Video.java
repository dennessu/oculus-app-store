/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Video.
 */
public class Video {
    @ApiModelProperty(position = 1, required = true, value = "Service provider",
            allowableValues = "YOUTUBE, VIMEO, OCULUS")
    private String service;

    @ApiModelProperty(position = 2, required = true, value = "The id of the video")
    private String id;

    @ApiModelProperty(position = 3, required = true, value = "The title of the video")
    private String title;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
