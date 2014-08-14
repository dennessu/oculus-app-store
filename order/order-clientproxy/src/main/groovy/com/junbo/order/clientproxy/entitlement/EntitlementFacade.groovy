/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.entitlement

import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.entitlement.spec.model.Entitlement
import com.junbo.order.clientproxy.model.Offer

/**
 * Created by LinYi on 2014/8/14.
 */
public interface EntitlementFacade {
    Promise<List<Entitlement>> getEntitlements(UserId userId, Offer offer)
}