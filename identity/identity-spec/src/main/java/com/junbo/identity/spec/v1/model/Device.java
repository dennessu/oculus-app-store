/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.DeviceId;
import com.junbo.common.id.DeviceTypeId;
import com.junbo.common.model.Link;
import com.junbo.common.util.Identifiable;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Properties;

/**
 * Created by liangfu on 4/3/14.
 */
public class Device extends ResourceMeta implements Identifiable<DeviceId> {

    @ApiModelProperty(position = 1, required = true, value = "[Nullable]The id of device resource.")
    @JsonProperty("self")
    private DeviceId id;

    @ApiModelProperty(position = 2, required = true, value = "The type of the device.")
    private DeviceTypeId type;

    @ApiModelProperty(position = 3, required = true, value = "The serial number of the device.")
    private String serialNumber;

    @ApiModelProperty(position = 4, required = true, value = "The description of the device.")
    private String firmwareVersion;

    @ApiModelProperty(position = 5, required = false, value = "[Nullable]The users linked with this device")
    @JsonProperty("users")
    private Link users;

    @ApiModelProperty(position = 6, required = false, value = "Feature expansion of the device resource.")
    private Properties futureExpansion;

    public DeviceId getId() {
        return id;
    }

    public void setId(DeviceId id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public Link getUsers() {
        return users;
    }

    public void setUsers(Link users) {
        this.users = users;
    }

    public DeviceTypeId getType() {
        return type;
    }

    public void setType(DeviceTypeId type) {
        this.type = type;
    }

    public Properties getFutureExpansion() {
        return futureExpansion;
    }

    public void setFutureExpansion(Properties futureExpansion) {
        this.futureExpansion = futureExpansion;
    }
}
