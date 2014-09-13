/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.rest.resource;

import com.junbo.authorization.AuthorizeCallback;
import com.junbo.authorization.AuthorizeContext;
import com.junbo.authorization.AuthorizeService;
import com.junbo.authorization.RightsScope;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.auth.PaymentInstrumentAuthorizeCallbackFactory;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam;
import com.junbo.payment.spec.resource.PaymentInstrumentResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import java.util.ArrayList;
import java.util.List;
/**
 * payment instrument resource implementation.
 */
public class PaymentInstrumentResourceImpl implements PaymentInstrumentResource {
    @Autowired
    private PaymentInstrumentService piService;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private PaymentInstrumentAuthorizeCallbackFactory authorizeCallbackFactory;

    @Override
    public Promise<PaymentInstrument> postPaymentInstrument(final PaymentInstrument request) {
        CommonUtil.preValidation(request);
        AuthorizeCallback callback = authorizeCallbackFactory.create(request);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply() {
                if (!AuthorizeContext.hasRights("create")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }
                return piService.add(request).then(new Promise.Func<PaymentInstrument, Promise<PaymentInstrument>>() {
                    @Override
                    public Promise<PaymentInstrument> apply(PaymentInstrument paymentInstrument) {
                        CommonUtil.postFilter(paymentInstrument);
                        //filterSensitiveData(paymentInstrument);
                        return Promise.pure(paymentInstrument);
                    }
                });
            }
        });
        //request.setId(new PIId(userId.getValue(), null));
    }

    @Override
    public Promise<PaymentInstrument> getById(final PaymentInstrumentId paymentInstrumentId) {
        return piService.getById(paymentInstrumentId.getValue()).then(new Promise.Func<PaymentInstrument,
                Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(final PaymentInstrument paymentInstrument) {
                AuthorizeCallback callback = authorizeCallbackFactory.create(paymentInstrument);
                return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<PaymentInstrument>>() {
                    @Override
                    public Promise<PaymentInstrument> apply() {
                        if (!AuthorizeContext.hasRights("read")) {
                            throw AppClientExceptions.INSTANCE.paymentInstrumentNotFound(paymentInstrumentId.toString()).exception();
                        }
                        //filterSensitiveData(paymentInstrument);
                        return Promise.pure(paymentInstrument);
                    }
                });
            }
        });
    }

    @Override
    public Promise<Void> delete(final PaymentInstrumentId paymentInstrumentId) {
        piService.getById(paymentInstrumentId.getValue()).then(new Promise.Func<PaymentInstrument,
                Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(PaymentInstrument paymentInstrument) {
                AuthorizeCallback callback = authorizeCallbackFactory.create(paymentInstrument);
                return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<PaymentInstrument>>() {
                    @Override
                    public Promise<PaymentInstrument> apply() {
                        if (!AuthorizeContext.hasRights("delete")) {
                            throw AppCommonErrors.INSTANCE.forbidden().exception();
                        }

                        piService.delete(paymentInstrumentId.getValue());
                        return Promise.pure(null);
                    }
                });
            }
        });
        return Promise.pure(null);
    }

    @Override
    public Promise<PaymentInstrument> update(PaymentInstrumentId paymentInstrumentId,
                                             final PaymentInstrument request) {
        if(!paymentInstrumentId.getValue().equals(request.getId())){
            throw AppClientExceptions.INSTANCE.invalidPaymentInstrumentId(request.getId().toString()).exception();
        }

        AuthorizeCallback callback = authorizeCallbackFactory.create(request);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply() {
                if (!AuthorizeContext.hasRights("update")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                piService.update(request);
                //filterSensitiveData(request);
                return Promise.pure(request);
            }
        });
        //if(!userId.getValue().equals(request.getUserId())){
        //    throw AppClientExceptions.INSTANCE.invalidUserId(request.getId().toString()).exception();
        //}
    }

    @Override
    public Promise<Results<PaymentInstrument>> searchPaymentInstrument(@BeanParam final PaymentInstrumentSearchParam searchParam) {
        if(searchParam.getUserId() == null){
            throw AppCommonErrors.INSTANCE.fieldRequired("user_id").exception();
        }

        AuthorizeCallback callback = authorizeCallbackFactory.create(searchParam.getUserId().getValue());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Results<PaymentInstrument>>>() {
            @Override
            public Promise<Results<PaymentInstrument>> apply() {
                if (!AuthorizeContext.hasRights("read")) {
                    Results<PaymentInstrument> result = new Results<>();
                    result.setItems(new ArrayList<PaymentInstrument>());
                    return Promise.pure(result);
                }

                return piService.searchPi(searchParam.getUserId().getValue(), searchParam)
                        .then(new Promise.Func<List<PaymentInstrument>, Promise<Results<PaymentInstrument>>>() {
                            @Override
                            public Promise<Results<PaymentInstrument>> apply(List<PaymentInstrument> paymentInstruments) {
                                Results<PaymentInstrument> result = new Results<PaymentInstrument>();
                                result.setItems(paymentInstruments);
                                //result.setNext(CommonUtils.buildNextUrl(uriInfo));
                                /*
                                if(paymentInstruments != null){
                                    for(PaymentInstrument pi : paymentInstruments){
                                        filterSensitiveData(pi);
                                    }
                                }
                                */
                                return Promise.pure(result);
                            }
                        });
            }
        });
    }

    @GET
    public String ping() {
        return "hello payment";
    }

    private void filterSensitiveData(PaymentInstrument paymentInstrument) {
        if(!AuthorizeContext.hasRights("bin.read")){
            if(paymentInstrument.getTypeSpecificDetails() != null){
                paymentInstrument.getTypeSpecificDetails().setIssuerIdentificationNumber(null);
            }
        }
        if(!AuthorizeContext.hasRights("expireDate.read")){
            if(paymentInstrument.getTypeSpecificDetails() != null){
                paymentInstrument.getTypeSpecificDetails().setExpireDate(null);
            }
        }
    }
}
