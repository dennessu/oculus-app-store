/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.clientproxy;

import com.junbo.entitlement.spec.model.Entitlement;
/**
 * Created by Administrator on 14-3-27.
 */
public interface EntitlementGateway {
    String grant(Entitlement entitlement);
}
