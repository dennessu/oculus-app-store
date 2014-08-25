/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.drm.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.drm.spec.model.LicenseRequest;
import com.junbo.drm.spec.model.SignedLicense;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.drm.DrmService;

/**
 * @author Jason
 * Time: 7/28/2014
 * The implementation for drm related APIs
 */
public class DrmServiceImpl extends HttpClientBase implements DrmService {

    private final String drmURL = ConfigHelper.getSetting("defaultCommerceEndpoint") + "/licenses";
    private static DrmService instance;

    public static synchronized DrmService instance() {
        if (instance == null) {
            instance = new DrmServiceImpl();
        }
        return instance;
    }

    private DrmServiceImpl() {
        componentType = ComponentType.DRM;
    }

    public SignedLicense postLicense(LicenseRequest request) throws Exception {
        return postLicense(request, 200);
    }

    @Override
    public SignedLicense postLicense(LicenseRequest request, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, drmURL, request, expectedResponseCode, true);
        return new JsonMessageTranscoder().decode(new TypeReference<SignedLicense>() {}, responseBody);
    }
}
