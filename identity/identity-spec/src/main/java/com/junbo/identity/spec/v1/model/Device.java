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
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangfu on 4/3/14.
 */
public class Device extends PropertyAssignedAwareResourceMeta implements Identifiable<DeviceId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable]The id of device resource.")
    @JsonProperty("self")
    private DeviceId id;

    @ApiModelProperty(position = 2, required = true, value = "The Link to the device type of the device.")
    private DeviceTypeId type;

    @ApiModelProperty(position = 3, required = true, value = "The serial number of the device.")
    private String serialNumber;

    @ApiModelProperty(position = 4, required = true, value = "The description of the device.")
    private String firmwareVersion;

    @ApiModelProperty(position = 5, required = true, value = "[Nullable]The links to the component devices, for example HMD, camera, etc.")
    private List<DeviceId> components = new ArrayList<>();

    @ApiModelProperty(position = 5, required = false, value = "[Nullable]The users linked with this device")
    @HateoasLink("/users?deviceId={id}")
    private Link users;

    @ApiModelProperty(position = 6, required = false, value = "Feature expansion of the device resource.")
    private Map<String, String> futureExpansion = new HashMap<>();

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

    public Map<String, String> getFutureExpansion() {
        return futureExpansion;
    }

    public void setFutureExpansion(Map<String, String> futureExpansion) {
        this.futureExpansion = futureExpansion;
        support.setPropertyAssigned("futureExpansion");
    }
}
