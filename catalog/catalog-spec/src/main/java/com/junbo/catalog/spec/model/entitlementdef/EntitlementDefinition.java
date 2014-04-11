/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.entitlementdef;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.junbo.common.jackson.annotation.EntitlementDefinitionId;
import com.junbo.common.jackson.annotation.UserId;

import java.util.List;
import java.util.UUID;

/**
 * EntitlementDefinition Model.
 */
@JsonPropertyOrder(value = {
        "entitlementDefId",
        "rev",
        "developerId",
        "inAppContext",
        "type",
        "group",
        "tag",
        "consumable"
})
public class EntitlementDefinition {
    private UUID trackingUuid;
    @JsonProperty("self")
    @EntitlementDefinitionId
    private Long entitlementDefId;
    private Integer rev;
    @UserId
    @JsonProperty("developer")
    private Long developerId;
    private List<String> inAppContext;
    private String type;
    private String group;
    private String tag;
    private Boolean consumable;

    @JsonIgnore
    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    @JsonProperty
    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    public Long getEntitlementDefId() {
        return entitlementDefId;
    }

    public void setEntitlementDefId(Long entitlementDefId) {
        this.entitlementDefId = entitlementDefId;
    }

    public Integer getRev() {
        return rev;
    }

    public void setRev(Integer rev) {
        this.rev = rev;
    }

    public Long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Long developerId) {
        this.developerId = developerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Boolean getConsumable() {
        return consumable;
    }

    public void setConsumable(Boolean consumable) {
        this.consumable = consumable;
    }

    public List<String> getInAppContext() {
        return inAppContext;
    }

    public void setInAppContext(List<String> inAppContext) {
        this.inAppContext = inAppContext;
    }
}
