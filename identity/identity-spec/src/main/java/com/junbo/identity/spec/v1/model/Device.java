/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.enumid.DeviceTypeId;
import com.junbo.common.id.DeviceId;
import com.junbo.common.jackson.annotation.HateoasLink;
import com.junbo.common.model.Link;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 4/3/14.
 */
public class Device extends PropertyAssignedAwareResourceMeta<DeviceId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] A Link to this Device resource.")
    @JsonProperty("self")
    private DeviceId id;

    @ApiModelProperty(position = 2, required = true, value = "The Link to the DeviceType of this device.")
    private DeviceTypeId type;

    @ApiModelProperty(position = 3, required = true, value = "The serial number of this Device.")
    private String serialNumber;

    @ApiModelProperty(position = 4, required = true, value = "The firmware version on this Device.")
    private String firmwareVersion;

    @ApiModelProperty(position = 5, required = true, value = "[Nullable]The links to the component devices, " +
            "for example when the device object is \"DK2\" it will has component \"HMD\" and \"camera\", " +
            "when the device object itself is \"Camera\" the components attribute will be null.")
    private List<DeviceId> components = new ArrayList<>();

    @ApiModelProperty(position = 5, required = false, value = "[Client Immutable] The users linked with this device.")
    @HateoasLink("/users?deviceId={id}")
    private Link users;

    public DeviceId getId() {
        return id;
    }

    public void setId(DeviceId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        support.setPropertyAssigned("serialNumber");
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
        support.setPropertyAssigned("firmwareVersion");
    }

    public List<DeviceId> getComponents() {
        return components;
    }

    public void setComponents(List<DeviceId> components) {
        this.components = components;
        support.setPropertyAssigned("components");
    }

    public Link getUsers() {
        return users;
    }

    public void setUsers(Link users) {
        this.users = users;
        support.setPropertyAssigned("users");
    }

    public DeviceTypeId getType() {
        return type;
    }

    public void setType(DeviceTypeId type) {
        this.type = type;
        support.setPropertyAssigned("type");
    }
}
