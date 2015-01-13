/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model;

import com.junbo.common.id.UserId;
import com.junbo.identity.spec.v1.model.Country;
import com.junbo.identity.spec.v1.model.Currency;
import com.junbo.identity.spec.v1.model.Locale;

import java.util.Map;

/**
 * The ApiContext class.
 */
public class ApiContext {

    private Platform platform;

    private Locale locale;

    private Country country;

    private Currency currency;

    private String androidId;

    private String deviceId;

    private String userAgent;

    private UserId user;

    /**
     * The ContextDataKey enum.
     */
    public static enum ContextDataKey {
        CMS_CAMPAIGN
    }

    private Map<ContextDataKey, Object> contextData;

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public UserId getUser() {
        return user;
    }

    public void setUser(UserId user) {
        this.user = user;
    }

    public Map<ContextDataKey, Object> getContextData() {
        return contextData;
    }

    public void setContextData(Map<ContextDataKey, Object> contextData) {
        this.contextData = contextData;
    }
}
