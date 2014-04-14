/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog;

import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.libs.RestUrl;

/**
 @author Jason
  * Time: 4/9/2014
  * Implemetention of calling Item APIs
 */
public class ItemResource extends HttpClientBase {

    private static ItemResource instance;
    private final String catalogServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.CATALOG) + "items";

    public static synchronized ItemResource instance() {
        if (instance == null) {
            instance = new ItemResource();
        }
        return instance;
    }

    private ItemResource() {
    }

    public void deleteItem(String itemId) throws Exception {
        this.deleteItem(itemId, 204);
    }

    public void deleteItem(String itemId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + itemId;
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
    }
}
