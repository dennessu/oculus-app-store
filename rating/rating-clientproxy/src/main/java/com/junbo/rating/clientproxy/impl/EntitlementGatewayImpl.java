/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy.impl;

import com.junbo.entitlement.spec.resource.EntitlementResource;
import com.junbo.rating.clientproxy.EntitlementGateway;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lizwu on 2/27/14.
 */
public class EntitlementGatewayImpl implements EntitlementGateway {
    @Autowired
    private EntitlementResource entitlementResource;


}
