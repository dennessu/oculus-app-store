/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.rest.resource;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.spec.model.PageMetaData;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam;
import com.junbo.payment.spec.resource.PaymentInstrumentResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * payment instrument resource implementation.
 */
public class PaymentInstrumentResourceImpl implements PaymentInstrumentResource {
    @Autowired
    private PaymentInstrumentService piService;

    @Override
    public Promise<PaymentInstrument> postPaymentInstrument(PaymentInstrument request) {
        CommonUtil.preValidation(request);
        //request.setId(new PIId(userId.getValue(), null));
        return piService.add(request).then(new Promise.Func<PaymentInstrument, Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(PaymentInstrument paymentInstrument) {
                CommonUtil.postFilter(paymentInstrument);
                return Promise.pure(paymentInstrument);
            }
        });
    }

    @Override
    public Promise<PaymentInstrument> getById(PaymentInstrumentId paymentInstrumentId) {
        PaymentInstrument result = piService.getById(paymentInstrumentId.getValue());
        return Promise.pure(result);
    }

    @Override
    public Promise<Response> delete(PaymentInstrumentId paymentInstrumentId) {
        piService.delete(paymentInstrumentId.getValue());
        return Promise.pure(Response.status(204).build());
    }

    @Override
    public Promise<PaymentInstrument> update(PaymentInstrumentId paymentInstrumentId,
                                             PaymentInstrument request) {
        if(!paymentInstrumentId.getValue().equals(request.getId())){
            throw AppClientExceptions.INSTANCE.invalidPaymentInstrumentId(request.getId().toString()).exception();
        }
        //if(!userId.getValue().equals(request.getUserId())){
        //    throw AppClientExceptions.INSTANCE.invalidUserId(request.getId().toString()).exception();
        //}

        piService.update(request);
        return Promise.pure(request);
    }

    @Override
    public Promise<Results<PaymentInstrument>> searchPaymentInstrument(
            @BeanParam PaymentInstrumentSearchParam searchParam, @BeanParam PageMetaData pageMetadata) {
        List<PaymentInstrument> piRequests = piService.searchPi(searchParam.getUserId(), searchParam, pageMetadata);
        Results<PaymentInstrument> result = new Results<PaymentInstrument>();
        result.setItems(piRequests);
        //result.setNext(CommonUtils.buildNextUrl(uriInfo));
        return Promise.pure(result);
    }

    @GET
    public String ping() {
        return "hello payment";
    }
}
