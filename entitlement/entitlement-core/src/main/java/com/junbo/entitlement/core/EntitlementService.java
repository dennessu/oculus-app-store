/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.core;

import com.junbo.common.model.Results;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;

import java.net.URI;
import java.util.UUID;

/**
 * Interface of Entitlement Service.
 */
public interface EntitlementService {
    Entitlement getEntitlement(String entitlementId);

    Entitlement addEntitlement(Entitlement entitlement);

    Entitlement updateEntitlement(String entitlementId, Entitlement entitlement);

    void deleteEntitlement(String entitlementId);

    Results<Entitlement> searchEntitlement(EntitlementSearchParam entitlementSearchParam,
                                           PageMetadata pageMetadata);

    Entitlement getByTrackingUuid(Long shardMasterId, UUID trackingUuid);

    URI getDownloadUrl(String entitlementId, String itemId, String platform);
}
