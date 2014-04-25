/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.entitlementdef;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.junbo.common.jackson.annotation.ClientId;
import com.junbo.common.jackson.annotation.EntitlementDefinitionId;
import com.junbo.common.jackson.annotation.UserId;
import com.wordnik.swagger.annotations.ApiModelProperty;

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
    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] entitlementDefinition id")
    @JsonProperty("self")
    @EntitlementDefinitionId
    private Long entitlementDefId;
    @ApiModelProperty(position = 2, required = true, value = "[Client Immutable]")
    private String rev;
    @ApiModelProperty(position = 3, required = true, value = "the developer this entitlementDefinition belongs to")
    @UserId
    @JsonProperty("developer")
    private Long developerId;
    @ApiModelProperty(position = 4, required = true,
            value = "clients which entitlementDefinition can be allowed to use in")
    @ClientId
    @JsonProperty("checkClients")
    private List<String> inAppContext;
    @ApiModelProperty(position = 5, required = true,
            value = "type of entitlementDefinition.\n" +
                    "If having no type, it will be set to null.\n" +
                    "DEVELOPER is granted to a user when a user is upgraded to a developer.\n" +
                    "DEVELOPER_SUBSCRIPTION is DEVELOPER entitlement with subscription.\n" +
                    "DOWNLOAD is granted when a user buys an APP and shows the user can download the APP.\n" +
                    "DOWNLOAD_SUBSCRIPTION is DOWNLOAD entitlement with subscription.\n" +
                    "ONLINE_ACCESS shows whether a user can access some online games.\n" +
                    "ONLINE_ACCESS_SUBSCRIPTION is ONLINE_ACCESS entitlement with subscription.\n" +
                    "SUBSCRIPTION is subscription entitlements\n",
            allowableValues = "DOWNLOAD, DOWNLOAD_SUBSCRIPTION," +
                    " DEVELOPER, DEVELOPER_SUBSCRIPTION, ONLINE_ACCESS," +
                    " ONLINE_ACCESS_SUBSCRIPTION, SUBSCRIPTIONS")
    @JsonProperty("type")
    private String type;
    @ApiModelProperty(position = 6, required = true,
            value = "whether the entitlement with the entitlementDefinition can be consumable")
    @JsonProperty("isConsumable")
    private Boolean consumable;
    @ApiModelProperty(position = 7, required = true,
            value = "group of entitlementDefinition, for example, ARMOR_HELM or WEAPON_AXE")
    private String group;
    @ApiModelProperty(position = 8, required = true,
            value = "tag of entitlementDefinition, for example, LEATHER_HOOD or HAND_AXE")
    private String tag;

    @JsonIgnore
    private UUID trackingUuid;

    public Long getEntitlementDefId() {
        return entitlementDefId;
    }

    public void setEntitlementDefId(Long entitlementDefId) {
        this.entitlementDefId = entitlementDefId;
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

    public List<String> getInAppContext() {
        return inAppContext;
    }

    public void setInAppContext(List<String> inAppContext) {
        this.inAppContext = inAppContext;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public Boolean getConsumable() {
        return consumable;
    }

    public void setConsumable(Boolean consumable) {
        this.consumable = consumable;
    }

    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }
}
