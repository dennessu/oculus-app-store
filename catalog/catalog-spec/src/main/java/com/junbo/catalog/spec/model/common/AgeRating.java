/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Age rating.
 */
public class AgeRating {
    @ApiModelProperty(position = 1, required = true)
    private String category;
    @ApiModelProperty(position = 2, required = true)
    private List<String> descriptors;
    @ApiModelProperty(position = 3, required = true)
    private String certificate;
    @ApiModelProperty(position = 4, required = true)
    private Boolean online;
    @ApiModelProperty(position = 5, required = true)
    private Boolean provisional;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(List<String> descriptors) {
        this.descriptors = descriptors;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Boolean getProvisional() {
        return provisional;
    }

    public void setProvisional(Boolean provisional) {
        this.provisional = provisional;
    }
}
