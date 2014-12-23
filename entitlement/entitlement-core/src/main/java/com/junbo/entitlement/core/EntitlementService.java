/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.core;

import com.junbo.common.model.Results;
import com.junbo.entitlement.spec.model.DownloadUrlGetOptions;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;

import java.util.List;
import java.util.Map;

/**
 * Interface of Entitlement Service.
 */
public interface EntitlementService {
    Entitlement getEntitlement(String entitlementId);

    Entitlement addEntitlement(Entitlement entitlement);

    Map<Long, List<Entitlement>> addEntitlements(Map<Long, List<Entitlement>> entitlements);

    Entitlement updateEntitlement(String entitlementId, Entitlement entitlement);

    void deleteEntitlement(String entitlementId);

    Results<Entitlement> searchEntitlement(EntitlementSearchParam entitlementSearchParam,
                                           PageMetadata pageMetadata);

    String getDownloadUrl(String itemId, DownloadUrlGetOptions options);
}
