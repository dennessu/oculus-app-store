/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.entitlement;

import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.common.model.Results;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;

/**
 * Created by jiefeng on 14-3-25.
 */
public interface EntitlementService {

    Entitlement grantEntitlement(Entitlement entitlement) throws Exception;
    Entitlement grantEntitlement(Entitlement entitlement, int expectedResponseCode) throws Exception;

    Entitlement getEntitlement(String entitlementId) throws Exception;
    Entitlement getEntitlement(String entitlementId, int expectedResponseCode) throws Exception;

    Results<Entitlement> getEntitlements(String userId) throws Exception;
    Results<Entitlement> getEntitlements(String userId, int expectedResponseCode) throws Exception;

    Entitlement updateEntitlement(String entitlementId, Entitlement entitlement) throws Exception;
    Entitlement updateEntitlement(String entitlementId, Entitlement entitlement, int expectedResponseCode) throws Exception;

    void deleteEntitlement(String entitlementId) throws Exception;
    void deleteEntitlement(String entitlementId, int expectedResponseCode) throws Exception;

    String getDownloadUrl(String entitlementId, String itemId, String platform) throws Exception;
    String getDownloadUrl(String entitlementId, String itemId, String platform, int expectedResponseCode) throws Exception;

    String getDownloadUrlForItemRevision(String itemRevisionId, String itemId, String platform) throws Exception;
    String getDownloadUrlForItemRevision(String itemRevisionId, String itemId, String platform, int expectedResponseCode) throws Exception;

    Results<Entitlement> searchEntitlements(EntitlementSearchParam param, String cursor, Integer count) throws Exception;
    Results<Entitlement> searchEntitlements(EntitlementSearchParam param, String cursor, Integer count, int expectedResponseCode) throws Exception;

}
