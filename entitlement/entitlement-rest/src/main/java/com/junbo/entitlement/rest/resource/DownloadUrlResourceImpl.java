/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.rest.resource;

import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.ItemId;
import com.junbo.entitlement.core.EntitlementService;
import com.junbo.entitlement.spec.model.DownloadUrlResponse;
import com.junbo.entitlement.spec.resource.DownloadUrlResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * impl of DownloadUrlResource.
 */
public class DownloadUrlResourceImpl implements DownloadUrlResource {
    @Autowired
    private EntitlementService entitlementService;

    @Override
    public Promise<DownloadUrlResponse> getDownloadUrl(EntitlementId entitlementId, ItemId itemId, String platform) {
        if (entitlementId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("entitlementId").exception();
        }
        if (StringUtils.isEmpty(platform)) {
            throw AppCommonErrors.INSTANCE.fieldRequired("platform").exception();
        }
        String url = entitlementService.getDownloadUrl(entitlementId.getValue(), itemId.getValue(), platform);
        DownloadUrlResponse response = new DownloadUrlResponse();
        response.setRedirectUrl(url);
        return Promise.pure(response);
    }
}
