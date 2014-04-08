/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing.apihelper.impl;

import com.junbo.billing.spec.model.Balance;
import com.junbo.test.billing.apihelper.BalanceService;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RestUrl;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;

/**
 * Created by Yunlong on 4/8/14.
 */
public class BalanceServiceImpl extends HttpClientBase implements BalanceService {

    private static String balanceUrl = RestUrl.getRestUrl(RestUrl.ComponentName.COMMERCE);

    private LogHelper logger = new LogHelper(BalanceServiceImpl.class);

    private static BalanceService instance;

    public static synchronized BalanceService getInstance() {
        if (instance == null) {
            instance = new BalanceServiceImpl();
        }
        return instance;
    }

    @Override
    public String postBalance(String uid, Balance balance) throws Exception {
        return postBalance(uid, balance, 200);
    }

    @Override
    public String postBalance(String uid, Balance balance, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, balanceUrl + "users/" + uid + "/balances", balance);

        Balance balanceResult =
                new JsonMessageTranscoder().decode(
                        new TypeReference<Balance>() {
                        }, responseBody);

        String balanceId = IdConverter.idToHexString(balanceResult.getBalanceId());
        Master.getInstance().addBalances(balanceId, balanceResult);

        return null;
    }

    @Override
    public String getBalanceByBalanceId(String balanceId) throws Exception {
        return getBalanceByBalanceId(balanceId, 200);
    }

    @Override
    public String getBalanceByBalanceId(String balanceId, int expectedResponseCode) throws Exception {
        return null;
    }

    @Override
    public String getBalanceByOrderId(String orderId) throws Exception {
        return getBalanceByOrderId(orderId, 200);
    }

    @Override
    public String getBalanceByOrderId(String orderId, int expectedResponseCode) throws Exception {
        return null;
    }
}
