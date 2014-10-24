/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.clientproxy.entitlement;

import com.junbo.common.id.EntitlementId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.ApiContext;
import com.junbo.store.spec.model.Entitlement;

/**
 * The EntitlementFacade class.
 */
public interface EntitlementFacade {
    Promise<Entitlement> getDigitalEntitlement(EntitlementId entitlementId, boolean isIAP, ApiContext apiContext);
}
