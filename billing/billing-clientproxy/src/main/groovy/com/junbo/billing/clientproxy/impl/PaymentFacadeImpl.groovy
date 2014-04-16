/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl

import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.json.IdPathParamTranscoder
import com.junbo.common.json.JsonMessageTranscoder
import com.junbo.common.json.QueryParamTranscoderImpl
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.model.PaymentTransaction
import com.junbo.payment.spec.resource.proxy.PaymentInstrumentResourceClientProxy
import com.junbo.payment.spec.resource.proxy.PaymentTransactionResourceClientProxy
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncHttpClientConfigBean
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by xmchen on 2/20/14.
 */
@CompileStatic
class PaymentFacadeImpl implements PaymentFacade {

    @Autowired
    private final AsyncHttpClient asyncHttpClient

    private String url

    void setUrl(String url) {
        this.url = url
    }

    PaymentFacadeImpl() {
        if (asyncHttpClient == null) {
            asyncHttpClient = new AsyncHttpClient(new AsyncHttpClientConfigBean())
        }
    }

    @Override
    Promise<PaymentInstrument> getPaymentInstrument(Long piId) {
        return new PaymentInstrumentResourceClientProxy(asyncHttpClient, new JsonMessageTranscoder(),
               new IdPathParamTranscoder(), new QueryParamTranscoderImpl(), url)
                .getById(new PaymentInstrumentId(piId))
    }

    @Override
    Promise<PaymentTransaction> postPaymentCharge(PaymentTransaction request) {
        return new PaymentTransactionResourceClientProxy(asyncHttpClient, new JsonMessageTranscoder(),
                new IdPathParamTranscoder(), new QueryParamTranscoderImpl(), url).postPaymentCharge(request)
    }

    @Override
    Promise<PaymentTransaction> postPaymentAuthorization(PaymentTransaction request) {
        return new PaymentTransactionResourceClientProxy(asyncHttpClient, new JsonMessageTranscoder(),
                new IdPathParamTranscoder(), new QueryParamTranscoderImpl(), url).postPaymentAuthorization(request)
    }

    @Override
    Promise<PaymentTransaction> postPaymentCapture(Long paymentId, PaymentTransaction request) {
        return new PaymentTransactionResourceClientProxy(asyncHttpClient, new JsonMessageTranscoder(),
                new IdPathParamTranscoder(), new QueryParamTranscoderImpl(), url).postPaymentCapture(paymentId, request)
    }
}
