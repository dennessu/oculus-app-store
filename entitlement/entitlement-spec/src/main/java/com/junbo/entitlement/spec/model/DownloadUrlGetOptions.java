/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.spec.model;

import com.junbo.common.id.EntitlementId;

import javax.ws.rs.QueryParam;

/**
 * Download Url get options.
 */
public class DownloadUrlGetOptions {
    @QueryParam("entitlementId")
    private EntitlementId entitlementId;
    @QueryParam("platform")
    private String platform;
    @QueryParam("itemRevisionId")
    private String itemRevisionId;

    public EntitlementId getEntitlementId() {
        return entitlementId;
    }

    public void setEntitlementId(EntitlementId entitlementId) {
        this.entitlementId = entitlementId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getItemRevisionId() {
        return itemRevisionId;
    }

    public void setItemRevisionId(String itemRevisionId) {
        this.itemRevisionId = itemRevisionId;
    }
}
