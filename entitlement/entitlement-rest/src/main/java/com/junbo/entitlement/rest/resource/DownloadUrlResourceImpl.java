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
import com.junbo.entitlement.spec.resource.DownloadUrlResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * impl of DownloadUrlResource.
 */
public class DownloadUrlResourceImpl implements DownloadUrlResource {
    @Autowired
    private EntitlementService entitlementService;

    @Override
    public Promise<Response> getDownloadUrl(EntitlementId entitlementId, ItemId itemId, String platform) {
        if (entitlementId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("entitlementId").exception();
        }
        if (StringUtils.isEmpty(platform)) {
            throw AppCommonErrors.INSTANCE.fieldRequired("platform").exception();
        }
        URI url = entitlementService.getDownloadUrl(entitlementId.getValue(), itemId.getValue(), platform);
        return Promise.pure(Response.status(302).location(url).build());
    }
}
