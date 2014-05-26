/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.core.action;

import com.junbo.payment.spec.model.ChargeInfo;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.rating.spec.model.subscription.SubsRatingRequest;
import com.junbo.rating.spec.model.subscription.SubsRatingType;
import com.junbo.subscription.clientproxy.PaymentGateway;
import com.junbo.subscription.clientproxy.RatingGateway;
import com.junbo.subscription.core.SubscriptionAction;
import com.junbo.subscription.spec.model.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * Created by Administrator on 14-5-21.
 */
public class ChargeAction implements SubscriptionAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChargeAction.class);

    @Autowired
    private RatingGateway ratingGateway;

    @Autowired
    private PaymentGateway paymentGateway;

    @Override
    public Subscription execute(Subscription subscription){
        //Rating
        SubsRatingRequest request = new SubsRatingRequest();
        request.setCurrency(subscription.getCurrency());
        request.setType(SubsRatingType.PURCHASE);
        request.setOfferId(subscription.getOfferId());

        SubsRatingRequest response = ratingGateway.subsRating(request);

        PaymentTransaction paymentTransaction = new PaymentTransaction();

        paymentTransaction.setTrackingUuid(UUID.randomUUID());
        paymentTransaction.setUserId(subscription.getUserId());
        paymentTransaction.setPaymentInstrumentId(subscription.getPaymentMethodId());
        paymentTransaction.setBillingRefId(subscription.getSubscriptionId().toString());

        ChargeInfo chargeInfo = new ChargeInfo();
        chargeInfo.setCurrency(subscription.getCurrency());
        chargeInfo.setAmount(request.getAmount());
        chargeInfo.setCountry(subscription.getCountry());
        paymentTransaction.setChargeInfo(chargeInfo);

        //LOGGER.info('name=Charge_Balance. balance currency: {}, amount: {}, pi id: {}',balance.currency, balance.totalAmount, balance.piId);
        paymentTransaction = paymentGateway.chargePayment(paymentTransaction);

        return subscription;
    }

}
