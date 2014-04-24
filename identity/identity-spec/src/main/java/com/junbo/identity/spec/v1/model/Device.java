/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.DeviceId;
import com.junbo.common.util.Identifiable;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/3/14.
 */
public class Device extends ResourceMeta implements Identifiable<DeviceId> {

    @ApiModelProperty(position = 1, required = true, value = "The id of device resource.")
    @JsonProperty("self")
    private DeviceId id;

    // This is the unique external reference to device
    // such as device sequence id or something like that
    @ApiModelProperty(position = 2, required = true, value = "The external reference of the device.")
    private String externalRef;

    @ApiModelProperty(position = 3, required = true, value = "The description of the device.")
    private String description;

    // todo:    We may implement a lot of other fields

    @Override
    public DeviceId getId() {
        return id;
    }

    public void setId(DeviceId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getExternalRef() {
        return externalRef;
    }

    public void setExternalRef(String externalRef) {
        this.externalRef = externalRef;
        support.setPropertyAssigned("externalRef");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        support.setPropertyAssigned("description");
    }
}
