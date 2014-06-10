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
import com.junbo.subscription.common.util.Utils;
import com.junbo.subscription.core.SubscriptionActionService;
import com.junbo.subscription.db.entity.SubscriptionActionType;
import com.junbo.subscription.db.entity.SubscriptionStatus;
import com.junbo.subscription.db.repository.SubscriptionEventActionRepository;
import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.spec.model.SubscriptionEvent;
import com.junbo.subscription.spec.model.SubscriptionEventAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * Created by Administrator on 14-5-21.
 */
public class ChargeAction implements SubscriptionActionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChargeAction.class);

    @Autowired
    private SubscriptionEventActionRepository subscriptionEventActionRepository;

    @Autowired
    private RatingGateway ratingGateway;

    @Autowired
    private PaymentGateway paymentGateway;

    @Override
    public Subscription execute(Subscription subscription, SubscriptionEvent event){
        SubscriptionEventAction action = new SubscriptionEventAction();
        action.setSubscriptionId(subscription.getSubscriptionId());
        action.setSubscriptionEventId(event.getSubscriptionEventId());

        //Rating
        SubsRatingRequest request = new SubsRatingRequest();
        //request.(subscription.getCountry());
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

        action.setRequest(Utils.toJson(paymentTransaction, null));
        //LOGGER.info('name=Charge_Balance. balance currency: {}, amount: {}, pi id: {}',balance.currency, balance.totalAmount, balance.piId);
        paymentTransaction = paymentGateway.chargePayment(paymentTransaction);

        action.setResponse(Utils.toJson(paymentTransaction, null));
        action.setActionType(SubscriptionActionType.CHARGE.toString());
        action.setActionStatus(SubscriptionStatus.SUCCEEDED.toString());

        subscriptionEventActionRepository.insert(action);

        return subscription;
    }


}
