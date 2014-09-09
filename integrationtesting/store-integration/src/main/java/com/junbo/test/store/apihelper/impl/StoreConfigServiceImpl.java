/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper.impl;

import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.store.apihelper.StoreConfigService;

/**
 * The StoreConfigServiceImpl class.
 */
public class StoreConfigServiceImpl extends HttpClientBase implements StoreConfigService {

    private static String baseUrl = ConfigHelper.getSetting("defaultCommerceEndpoint") + "/store/config";

    private static StoreConfigService instance;

    public static synchronized StoreConfigService getInstance() {
        if (instance == null) {
            instance = new StoreConfigServiceImpl();
        }
        return instance;
    }

    @Override
    public void clearCache() throws Exception {
        restApiCall(HTTPMethod.POST, baseUrl + "/clearCache", null, 200);
    }
}
