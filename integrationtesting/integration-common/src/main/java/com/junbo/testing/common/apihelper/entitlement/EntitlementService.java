/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.entitlement;

import com.junbo.entitlement.spec.model.Entitlement;

import java.util.List;

/**
 * Created by jiefeng on 14-3-25.
 */
public interface EntitlementService {

    String grantEntitlement(Entitlement entitlement) throws Exception;

    String grantEntitlement(Entitlement entitlement, int expectedResponseCode) throws Exception;

    String getEntitlement(String entitlementId) throws Exception;

    List<String> getEntitlements(String userId, String developerId) throws Exception;

    List<String> getEntitlements(String userId, String developerId, int expectedResponseCode) throws Exception;
}
