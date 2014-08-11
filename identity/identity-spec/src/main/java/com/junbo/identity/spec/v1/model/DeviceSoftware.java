/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 8/7/2014.
 */
public class DeviceSoftware {
    @ApiModelProperty(position = 1, required = false, value = "the information for in develop version of device software")
    private SoftwareObject dev;

    @ApiModelProperty(position = 2, required = false, value = "the information for the stable version of device software")
    private SoftwareObject stable;

    public SoftwareObject getDev() {
        return dev;
    }

    public void setDev(SoftwareObject dev) {
        this.dev = dev;
    }

    public SoftwareObject getStable() {
        return stable;
    }

    public void setStable(SoftwareObject stable) {
        this.stable = stable;
    }
}
