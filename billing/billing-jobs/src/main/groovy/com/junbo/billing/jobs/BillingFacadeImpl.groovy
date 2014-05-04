/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.jobs

import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.resource.proxy.BalanceResourceClientProxy
import com.junbo.common.json.IdPathParamTranscoder
import com.junbo.common.json.JsonMessageTranscoder
import com.junbo.common.json.QueryParamTranscoderImpl
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncHttpClientConfigBean
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by xmchen on 14-4-11.
 */
@CompileStatic
class BillingFacadeImpl implements BillingFacade {

    @Autowired
    private final AsyncHttpClient asyncHttpClient

    private String url

    void setUrl(String url) {
        this.url = url
    }

    BillingFacadeImpl() {
        if (asyncHttpClient == null) {
            asyncHttpClient = new AsyncHttpClient(new AsyncHttpClientConfigBean())
        }
    }

    @Override
    Promise<Balance> processAsyncBalance(Balance balance) {
        return new BalanceResourceClientProxy(asyncHttpClient, new JsonMessageTranscoder(),
                new IdPathParamTranscoder(), new QueryParamTranscoderImpl(), url).processAsyncBalance(balance)
    }
}
