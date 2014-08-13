/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.iap;

import javax.ws.rs.QueryParam;

/**
 * The IAPEntitlementGetRequest class.
 */
public class IAPEntitlementGetRequest {

    @QueryParam("packageName")
    private String packageName;

    @QueryParam("packageVersion")
    private String packageVersion;

    @QueryParam("packageSignatureHash")
    private String packageSignatureHash;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public String getPackageSignatureHash() {
        return packageSignatureHash;
    }

    public void setPackageSignatureHash(String packageSignatureHash) {
        this.packageSignatureHash = packageSignatureHash;
    }
}
