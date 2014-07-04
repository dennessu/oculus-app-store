/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.enumid.DeviceTypeId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiali_000 on 4/21/2014.
 */
public class DeviceType extends PropertyAssignedAwareResourceMeta<DeviceTypeId> {

    @ApiModelProperty(position = 1, required = true, value = "[Nullable] The link to the device type resource.")
    @JsonProperty("self")
    private DeviceTypeId id;

    @ApiModelProperty(position = 2, required = true, value = "[Client Immutable] The code of this DeviceType; " +
            "this is an enumeration (DK1, DKHD, DK2, CV1, HMD, DK2_CAMERA, etc.) and is always the same as self.id.")
    private String typeCode;

    @ApiModelProperty(position = 3, required = true, value = "Map from available firmware version to firmware URL for this DeviceType.")
    private Map<String, String> availableFirmwares = new HashMap<>();

    @ApiModelProperty(position = 4, required = true, value = "[Nullable] The static URL for Instruction Manual.")
    private String instructionManual;

    @ApiModelProperty(position = 5, required = false, value = "The array of component type list.")
    private List<DeviceTypeId> componentTypes = new ArrayList<>();

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

    public List<DeviceTypeId> getComponentTypes() {
        return componentTypes;
    }

    public void setComponentTypes(List<DeviceTypeId> componentTypes) {
        this.componentTypes = componentTypes;
        support.setPropertyAssigned("componentTypes");
    }
}
