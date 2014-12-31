/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by dell on 12/30/2014.
 */
public class Measure {

    @ApiModelProperty(position = 1, required = false, value = "This is the height of the user.")
    private Double height;

    @ApiModelProperty(position = 2, required = false, value = "Interpupillary distance (IPD) is the distance between the center of the pupils of the two eyes.")
    @JsonProperty("IPD")
    private Double ipd;

    @ApiModelProperty(position = 3, required = false, value = "This is the real world height from the bottom of the user's feet to their eyes. " +
            "For rendering, this ends up being more important than the user's actual height.")
    private Double eyeHeight;

    @ApiModelProperty(position = 4, required = false, value = "This is the horizontal distance from the pivot at the base of the neck to the center point between the pupils.")
    private Double neckToEyeHorizontal;

    @ApiModelProperty(position = 5, required = false, value = "This is the vertical distance from the pivot at the base of the neck to the center point between the pupils.")
    private Double neckToEyeVertical;

    @ApiModelProperty(position = 6, required = false, value = "This is essentially the distance from a pupil to the screen. " +
            "All values are scalar and should have 0.1mm accuracy (no real preference as to if they're ints, floats, etc. as long as they're consistent in type and units).")
    private Double eyeRelief;

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getIpd() {
        return ipd;
    }

    public void setIpd(Double ipd) {
        this.ipd = ipd;
    }

    public Double getEyeHeight() {
        return eyeHeight;
    }

    public void setEyeHeight(Double eyeHeight) {
        this.eyeHeight = eyeHeight;
    }

    public Double getNeckToEyeHorizontal() {
        return neckToEyeHorizontal;
    }

    public void setNeckToEyeHorizontal(Double neckToEyeHorizontal) {
        this.neckToEyeHorizontal = neckToEyeHorizontal;
    }

    public Double getNeckToEyeVertical() {
        return neckToEyeVertical;
    }

    public void setNeckToEyeVertical(Double neckToEyeVertical) {
        this.neckToEyeVertical = neckToEyeVertical;
    }

    public Double getEyeRelief() {
        return eyeRelief;
    }

    public void setEyeRelief(Double eyeRelief) {
        this.eyeRelief = eyeRelief;
    }
}
