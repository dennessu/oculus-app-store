/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.enumid.DeviceTypeId;
import com.junbo.common.model.ResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiali_000 on 4/21/2014.
 */
public class DeviceType extends ResourceMeta implements Identifiable<DeviceTypeId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable]The id of the device type resource.")
    @JsonProperty("self")
    private DeviceTypeId id;

    @ApiModelProperty(position = 2, required = true, value = "[Client Immutable]The description of the device Type")
    private String typeCode;

    @ApiModelProperty(position = 3, required = true, value = "[Nullable] The available firmware list for the device type.")
    private Map<String, String> availableFirmwares = new HashMap<>();

    @ApiModelProperty(position = 4, required = true, value = "[Nullable] The static url for instruction Manual.")
    private String instructionManual;

    @ApiModelProperty(position = 5, required = true, value = "[Nullable] The array of component type list.")
    private List<DeviceType> componentTypes = new ArrayList<>();

    @ApiModelProperty(position = 6, required = false, value = "Feature expansion of the device type resource.")
    private Map<String, String> futureExpansion = new HashMap<>();

    public DeviceTypeId getId() {
        return id;
    }

    public void setId(DeviceTypeId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
        support.setPropertyAssigned("typeCode");
    }

    public Map<String, String> getAvailableFirmwares() {
        return availableFirmwares;
    }

    public void setAvailableFirmwares(Map<String, String> availableFirmwares) {
        this.availableFirmwares = availableFirmwares;
        support.setPropertyAssigned("availableFirmwares");
    }

    public String getInstructionManual() {
        return instructionManual;
    }

    public void setInstructionManual(String instructionManual) {
        this.instructionManual = instructionManual;
        support.setPropertyAssigned("instructionManual");
    }

    public List<DeviceType> getComponentTypes() {
        return componentTypes;
    }

    public void setComponentTypes(List<DeviceType> componentTypes) {
        this.componentTypes = componentTypes;
        support.setPropertyAssigned("componentTypes");
    }

    public Map<String, String> getFutureExpansion() {
        return futureExpansion;
    }

    public void setFutureExpansion(Map<String, String> futureExpansion) {
        this.futureExpansion = futureExpansion;
        support.setPropertyAssigned("futureExpansion");
    }
}
