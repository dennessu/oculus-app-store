package com.junbo.test.store.apihelper.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.store.apihelper.LoginService;
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
