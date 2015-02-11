/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.listener.clientproxy

import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.resource.BalanceResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

import javax.annotation.Resource

/**
 * Created by xmchen on 14-4-11.
 */
@CompileStatic
class BillingFacadeImpl implements BillingFacade {

    @Resource(name = 'billingBalanceClient')
    private BalanceResource balanceResource

    @Override
    Promise<Balance> processAsyncBalance(Balance balance) {
        return balanceResource.processAsyncBalance(balance)
    }

    @Override
    Promise<Balance> checkBalance(Balance balance) {
        return balanceResource.checkBalance(balance)
    }
}
