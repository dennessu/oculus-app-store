/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing.apihelper;

import com.junbo.billing.spec.model.Balance;

/**
 * Created by Yunlong on 4/8/14.
 */
public interface BalanceService {
    String postBalance(String uid, Balance balance) throws Exception;

    String postBalance(String uid, Balance balance, int expectedResponseCode) throws Exception;

    String getBalanceByBalanceId(String uid, String balanceId) throws Exception;

    String getBalanceByBalanceId(String uid, String balanceId, int expectedResponseCode) throws Exception;

    String getBalanceByOrderId(String uid, String orderId) throws Exception;

    String getBalanceByOrderId(String uid, String orderId, int expectedResponseCode) throws Exception;

}
