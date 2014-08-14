/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.iap;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.LocaleId;

import javax.ws.rs.QueryParam;

/**
 * The IAPOfferGetRequest class.
 */
public class IAPOfferGetRequest {
    @QueryParam("packageName")
    private String packageName;

    @QueryParam("packageVersion")
    private String packageVersion;

    @QueryParam("packageSignatureHash")
    private String packageSignatureHash;

    @QueryParam("locale")
    private LocaleId locale;

    @QueryParam("country")
    private CountryId country;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public LocaleId getLocale() {
        return locale;
    }

    public void setLocale(LocaleId locale) {
        this.locale = locale;
    }

    public CountryId getCountry() {
        return country;
    }

    public void setCountry(CountryId country) {
        this.country = country;
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
