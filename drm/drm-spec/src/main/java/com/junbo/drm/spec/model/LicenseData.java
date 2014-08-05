/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.drm.spec.model;

/**
 * drm.
 */
public class LicenseData {

    public static final String LICENSED = "LICENSED";

    public static final String NOT_LICENSED = "NOT_LICENSED";

    private String reasonCode;

    private String nonce;

    private String packageName;

    private String versionCode;

    private String deviceId;

    private String userId;

    private long validUntil;

    private long retryUntil;

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(long validUntil) {
        this.validUntil = validUntil;
    }

    public long getRetryUntil() {
        return retryUntil;
    }

    public void setRetryUntil(long retryUntil) {
        this.retryUntil = retryUntil;
    }
}
