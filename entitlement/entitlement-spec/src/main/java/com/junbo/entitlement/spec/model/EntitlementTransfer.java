/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.EntitlementId;
import com.junbo.common.jackson.annotation.UserId;

import java.util.UUID;

/**
 * EntitlementTransfer Model.
 */
public class EntitlementTransfer {
    private UUID trackingUuid;
    @UserId
    @JsonProperty("targetUser")
    private Long targetUserId;
    @EntitlementId
    @JsonProperty("entitlement")
    private String entitlementId;

    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getEntitlementId() {
        return entitlementId;
    }

    public void setEntitlementId(String entitlementId) {
        this.entitlementId = entitlementId;
    }
}
