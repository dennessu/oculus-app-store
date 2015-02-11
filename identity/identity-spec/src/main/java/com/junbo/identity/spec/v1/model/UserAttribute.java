/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.cloudant.json.annotations.CloudantIgnore;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.id.UserAttributeDefinitionId;
import com.junbo.common.id.UserAttributeId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by xiali_000 on 2014/12/19.
 */
public class UserAttribute extends PropertyAssignedAwareResourceMeta<UserAttributeId> {
    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable]Link to the user Attribute.")
    @JsonProperty("self")
    private UserAttributeId id;

    @ApiModelProperty(position = 2, required = true, value = "Link to the user")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 3, required = true, value = "Link to the user Attribute Definition")
    @JsonProperty("definition")
    private UserAttributeDefinitionId userAttributeDefinitionId;

    @ApiModelProperty(position = 4, required = false, value = "[Client Immutable], copy of the organization link in the attribute definition.")
    @JsonProperty("organization")
    private OrganizationId organizationId;

    @ApiModelProperty(position = 4, required = false, value = "[Client Immutable], copy of the type in the corresponding attribute definition")
    private String type;

    @ApiModelProperty(position = 4, required = false, value = "[Nullable] Value of the user attribute")
    private JsonNode value;

    @ApiModelProperty(position = 4, required = true, value = "[Client Immutable] True if/only if the user-attribute is active; " +
            "false when the user attribute is out of date, useCount is 0, or someone manually set isSuspended to true,")
    @CloudantIgnore
    private Boolean isActive;

    @ApiModelProperty(position = 5, required = false, value = "True if/only if this user attribute is suspended, e.g., by CSR or other authorized agent,")
    private Boolean isSuspended;

    @ApiModelProperty(position = 6, required = false, value = "The timestamp when this user attribute was granted; must be ISO 8601,")
    private Date grantTime;

    @ApiModelProperty(position = 7, required = false, value = "The timestamp when this user attribute expires (must be ISO 8601), or null to mean it never expires")
    private Date expirationTime;

    @ApiModelProperty(position = 8, required = false, value = "A non-negative number if this is a consumable user attribute; otherwise null")
    private Integer useCount;

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("user");
        support.setPropertyAssigned("userId");
    }

    public void setUserAttributeDefinitionId(UserAttributeDefinitionId userAttributeDefinitionId) {
        this.userAttributeDefinitionId = userAttributeDefinitionId;
        support.setPropertyAssigned("definition");
        support.setPropertyAssigned("userAttributeDefinitionId");
    }

    public JsonNode getValue() {
        return value;
    }

    public void setValue(JsonNode value) {
        this.value = value;
        support.setPropertyAssigned("value");
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(OrganizationId organizationId) {
        this.organizationId = organizationId;
        support.setPropertyAssigned("organizationId");
        support.setPropertyAssigned("organization");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        support.setPropertyAssigned("type");
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        support.setPropertyAssigned("isActive");
    }

    public void setIsSuspended(Boolean isSuspended) {
        this.isSuspended = isSuspended;
        support.setPropertyAssigned("isSuspended");
    }

    public void setGrantTime(Date grantTime) {
        this.grantTime = grantTime;
        support.setPropertyAssigned("grantTime");
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
        support.setPropertyAssigned("expirationTime");
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
        support.setPropertyAssigned("useCount");
    }

    public UserId getUserId() {
        return userId;
    }

    public UserAttributeDefinitionId getUserAttributeDefinitionId() {
        return userAttributeDefinitionId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public Boolean getIsSuspended() {
        return isSuspended;
    }

    public Date getGrantTime() {
        return grantTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public Integer getUseCount() {
        return useCount;
    }

    @Override
    public UserAttributeId getId() {
        return id;
    }

    @Override
    public void setId(UserAttributeId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }
}
