/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.drm.spec.model;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * drm.
 */
public class LicenseRequest {
    @ApiModelProperty(position = 1, required = true, value = "The Android Package Name.")
    private String packageName;

    @ApiModelProperty(position = 2, required = true, value = "The version code of the package.")
    private String versionCode;

    @ApiModelProperty(position = 3, required = false, value = "A random string")
    private String nonce;

    @ApiModelProperty(position = 4, required = true, value = "The Android Device Id.")
    private String deviceId;

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

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
