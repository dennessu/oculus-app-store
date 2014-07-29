/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.rest.resource;

import com.junbo.common.id.ItemId;
import com.junbo.entitlement.core.EntitlementService;
import com.junbo.entitlement.spec.model.DownloadUrlGetOptions;
import com.junbo.entitlement.spec.model.DownloadUrlResponse;
import com.junbo.entitlement.spec.resource.DownloadUrlResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * impl of DownloadUrlResource.
 */
public class DownloadUrlResourceImpl implements DownloadUrlResource {
    @Autowired
    private EntitlementService entitlementService;

    @Override
    public Promise<DownloadUrlResponse> getDownloadUrl(ItemId itemId, DownloadUrlGetOptions options) {
        String url = entitlementService.getDownloadUrl(itemId.getValue(), options);
        DownloadUrlResponse response = new DownloadUrlResponse();
        response.setRedirectUrl(url);
        return Promise.pure(response);
    }
}
