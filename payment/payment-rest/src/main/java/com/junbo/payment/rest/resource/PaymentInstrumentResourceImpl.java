/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.rest.resource;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.PreValidationException;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.core.exception.AppClientExceptions;
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
 * payment instrument resource implementation.
 */
public class PaymentInstrumentResourceImpl implements PaymentInstrumentResource {
    @Autowired
    private PaymentInstrumentService piService;

    @Override
    public Promise<PaymentInstrument> postPaymentInstrument(UserId userId, PaymentInstrument request) {
        try{
            CommonUtil.preValidation(request);}
        catch (PreValidationException ex){
            throw AppClientExceptions.INSTANCE.fieldNotNeeded(ex.getField()).exception();
        }
        if(request.getUserId() != null && !request.getUserId().equals(userId.getValue())){
            throw AppClientExceptions.INSTANCE.invalidUserId(request.getUserId().toString()).exception();
        }
        request.setUserId(userId.getValue());

        return piService.add(request).then(new Promise.Func<PaymentInstrument, Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(PaymentInstrument paymentInstrument) {
                CommonUtil.postFilter(paymentInstrument);
                return Promise.pure(paymentInstrument);
            }
        });
    }

    @Override
    public Promise<PaymentInstrument> getById(UserId userId, PaymentInstrumentId paymentInstrumentId) {
        PaymentInstrument result = piService.getById(userId.getValue(), paymentInstrumentId.getValue());
        return Promise.pure(result);
    }

    @Override
    public Promise<Response> delete(UserId userId, PaymentInstrumentId paymentInstrumentId) {
        piService.delete(userId.getValue(), paymentInstrumentId.getValue());
        return Promise.pure(Response.status(204).build());
    }

    @Override
    public Promise<PaymentInstrument> update(UserId userId, PaymentInstrumentId paymentInstrumentId,
                                             PaymentInstrument request) {
        if(!paymentInstrumentId.getValue().equals(request.getId())){
            throw AppClientExceptions.INSTANCE.invalidPaymentInstrumentId(request.getId().toString()).exception();
        }
        request.setUserId(userId.getValue());
        piService.update(request);
        return Promise.pure(request);
    }

    @Override
    public Promise<ResultList<PaymentInstrument>> searchPaymentInstrument(UserId userId,
            @BeanParam PaymentInstrumentSearchParam searchParam, @BeanParam PageMetaData pageMetadata) {
        List<PaymentInstrument> piRequests = piService.searchPi(userId.getValue(), searchParam, pageMetadata);
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
