/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.core;

import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.EntitlementTransfer;
import com.junbo.entitlement.spec.model.PageMetadata;

import java.util.List;
import java.util.UUID;

/**
 * Interface of Entitlement Service.
 */
public interface EntitlementService {
    Entitlement getEntitlement(Long entitlementId);

    Entitlement addEntitlement(Entitlement entitlement);

    Entitlement updateEntitlement(Long entitlementId, Entitlement entitlement);

    void deleteEntitlement(Long entitlementId, String reason);

    List<Entitlement> searchEntitlement(EntitlementSearchParam entitlementSearchParam,
                                        PageMetadata pageMetadata);

    Entitlement transferEntitlement(EntitlementTransfer entitlementTransfer);

    Entitlement getByTrackingUuid(UUID trackingUuid);
}
