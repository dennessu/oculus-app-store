/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import java.util.UUID;

/**
 * Created by chriszhu on 2/26/14.
 */
public class BaseEventModel extends BaseModelWithDate {
    private String action;
    private String status;
    private UUID trackingUuid;
    private String properties;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }
}
