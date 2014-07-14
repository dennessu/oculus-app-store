/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.iap;

import javax.ws.rs.QueryParam;

/**
 * The IAPOfferGetRequest class.
 */
public class IAPOfferGetRequest {
    @QueryParam("packageName")
    private String packageName;

    @QueryParam("type")
    private String type;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
