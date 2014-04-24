/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.rest.resource;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.core.PaymentCallbackService;
import com.junbo.payment.spec.enums.PropertyField;
import com.junbo.payment.spec.resource.PaymentCallbackResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * payment callback resource implementation.
 */
public class PaymentCallbackResourceImpl implements PaymentCallbackResource{
    @Autowired
    private PaymentCallbackService paymentCallbackService;

    @Override
    public Promise<Response> postPaymentProperties(Long paymentId, Map<String, String> properties) {
        Map<PropertyField, String> paymentProperties = new HashMap<>();
        for(Map.Entry property : properties.entrySet()){
            PropertyField field = null;
            try{
                field = PropertyField.valueOf(property.getKey().toString());
            }catch (Exception ex){
                throw AppClientExceptions.INSTANCE.invalidPropertyField(property.getKey().toString()).exception();
            }
            paymentProperties.put(field, property.getValue().toString());
        }
        paymentCallbackService.addPaymentProperties(paymentId, paymentProperties);
        return Promise.pure(Response.status(202).build());
    }
}