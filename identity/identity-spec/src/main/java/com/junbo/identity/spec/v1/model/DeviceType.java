/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.DeviceTypeId;
import com.junbo.common.util.Identifiable;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by xiali_000 on 4/21/2014.
 */
public class DeviceType extends ResourceMeta implements Identifiable<DeviceTypeId> {

    @ApiModelProperty(position = 1, required = true, value = "[Nullable] The id of the device type resource.")
    @JsonProperty("self")
    private DeviceTypeId id;

    @ApiModelProperty(position = 2, required = true, value = "The description of the device Type.")
    private String description;

    public DeviceTypeId getId() {
        return id;
    }

    public void setId(DeviceTypeId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        support.setPropertyAssigned("description");
    }
}
