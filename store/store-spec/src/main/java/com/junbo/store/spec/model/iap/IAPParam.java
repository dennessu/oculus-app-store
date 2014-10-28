/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.iap;

/**
 * The IAPParam class.
 */
public class IAPParam {

    private String packageName;
    private Integer packageVersion;
    private String packageSignatureHash;

    public IAPParam() {

    }

    public IAPParam(String packageName, Integer packageVersion, String packageSignatureHash) {
        this.packageName = packageName;
        this.packageVersion = packageVersion;
        this.packageSignatureHash = packageSignatureHash;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Integer getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(Integer packageVersion) {
        this.packageVersion = packageVersion;
    }

    public String getPackageSignatureHash() {
        return packageSignatureHash;
    }

    public void setPackageSignatureHash(String packageSignatureHash) {
        this.packageSignatureHash = packageSignatureHash;
    }
}
