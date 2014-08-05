/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.rest

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.drm.core.service.DrmService
import com.junbo.drm.spec.error.AppErrors
import com.junbo.drm.spec.model.LicenseRequest
import com.junbo.drm.spec.model.SignedLicense
import com.junbo.drm.spec.resource.DrmResource
import com.junbo.langur.core.promise.Promise
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * DrmResourceImpl.
 */
public class DrmResourceImpl implements DrmResource {

    private DrmService drmService;

    @Required
    public void setDrmService(DrmService drmService) {
        this.drmService = drmService;
    }

    @Override
    public Promise<SignedLicense> postLicense(LicenseRequest request) {
        validateRequest(request);

        UserId userId = AuthorizeContext.currentUserId
        if (userId == null) {
            throw AppErrors.INSTANCE.userNotFound().exception();
        }

        return drmService.createLicense(
                userId,
                request.packageName,
                request.versionCode,
                request.nonce,
                request.deviceId)
    }

    private static void validateRequest(LicenseRequest request) {
        if (StringUtils.isEmpty(request.getPackageName())) {
            throw AppCommonErrors.INSTANCE.fieldRequired("packageName").exception();
        }

        if (StringUtils.isEmpty(request.getVersionCode())) {
            throw AppCommonErrors.INSTANCE.fieldRequired("versionCode").exception();
        }

        if (StringUtils.isEmpty(request.getNonce())) {
            throw AppCommonErrors.INSTANCE.fieldRequired("nonce").exception();
        }

        if (StringUtils.isEmpty(request.getDeviceId())) {
            throw AppCommonErrors.INSTANCE.fieldRequired("deviceId").exception();
        }
    }
}
