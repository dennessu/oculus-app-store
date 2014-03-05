/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.rest.resource;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.spec.model.PageMetaData;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam;
import com.junbo.payment.spec.model.ResultList;
import com.junbo.payment.spec.resource.PaymentInstrumentResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * brain tree resource implimentation.
 */
public class PaymentInstrumentResourceImpl implements PaymentInstrumentResource {
    @Autowired
    private PaymentInstrumentService piService;

    @Override
    public Promise<PaymentInstrument> postPaymentInstrument(PaymentInstrument request) {
        return piService.add(request);
    }

    @Override
    public Promise<PaymentInstrument> getById(Long paymentInstrumentId) {
        PaymentInstrument result = piService.getById(paymentInstrumentId);
        return Promise.pure(result);
    }

    @Override
    public Promise<Response> delete(Long paymentInstrumentId) {
        piService.delete(paymentInstrumentId);
        return Promise.pure(Response.status(204).build());
    }

    @Override
    public Promise<PaymentInstrument> update(Long paymentInstrumentId, PaymentInstrument request) {
        request.setId(paymentInstrumentId);
        piService.update(request);
        return Promise.pure(request);
    }

    @Override
    public Promise<ResultList<PaymentInstrument>> searchPaymentInstrument(
            @BeanParam PaymentInstrumentSearchParam searchParam, @BeanParam PageMetaData pageMetadata) {
        List<PaymentInstrument> piRequests = piService.searchPi(searchParam, pageMetadata);
        ResultList<PaymentInstrument> result = new ResultList<PaymentInstrument>();
        result.setResults(piRequests);
        //result.setNext(CommonUtils.buildNextUrl(uriInfo));
        return Promise.pure(result);
    }

    @GET
    public String ping() {
        return "hello payment";
    }
}
