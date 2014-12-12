/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.fulfilment.utility;


import com.junbo.common.id.OrderId;
import com.junbo.fulfilment.spec.model.FulfilmentAction;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderItem;
import com.junbo.test.common.Utility.ValidationHelper;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.IdConverter;

/**
 * Created by yunlongzhao on 5/14/14.
 */
public class FulfimentValidationHelper extends ValidationHelper {
    public static final String SYSTEM_INTERNAL = "system internal";

    // constant number
    public static final int UNIQUE_RESULT = 0;

    // offer action properties
    public static final String ENTITLEMENT_DEF_ID = "ENTITLEMENT_DEF_ID";
    public static final String ITEM_ID = "ITEM_ID";
    public static final String STORED_VALUE_CURRENCY = "STORED_VALUE_CURRENCY";
    public static final String STORED_VALUE_AMOUNT = "STORED_VALUE_AMOUNT";
    public static final String USE_COUNT = "USE_COUNT";

    // event name
    public static final String EVENT_PURCHASE = "PURCHASE";

    // action name
    public static final String ACTION_GRANT_ENTITLEMENT = "GRANT_ENTITLEMENT";
    public static final String ACTION_DELIVER_PHYSICAL_GOODS = "DELIVER_PHYSICAL_GOODS";
    public static final String ACTION_CREDIT_WALLET = "CREDIT_WALLET";

    public void validateFulfilmentRequest(String fulfilmentRequestId, String orderId, boolean hasPhysicalGood) {
        FulfilmentRequest fulfilment = Master.getInstance().getFulfilment(fulfilmentRequestId);
        Order order = Master.getInstance().getOrder(orderId);
        verifyEqual(fulfilment.getOrderId(), IdConverter.hexStringToId(OrderId.class, orderId), "verify order id");
        verifyEqual(fulfilment.getUserId(), order.getUser().getValue(), "verify user id");
        if (hasPhysicalGood) {
            verifyEqual(fulfilment.getShippingAddressId(),
                    order.getShippingAddress().getValue(), "verify shipping address");
            verifyEqual(fulfilment.getShippingMethodId(), order.getShippingMethod(), "verify shipping method");
        }
        verifyEqual(fulfilment.getItems().size(), order.getOrderItems().size(), "verify fulfilment item size");

        for (FulfilmentItem fulfilmentItem : fulfilment.getItems()) {
            OrderItem orderItem = getOrderItemByOfferId(fulfilmentItem.getOfferId(), order);
            verifyEqual(fulfilmentItem.getQuantity(), orderItem.getQuantity(), "verify quantity");
            for (FulfilmentAction fulfilmentAction : fulfilmentItem.getActions()) {
                if (hasPhysicalGood) {
                    verifyEqual(fulfilmentAction.getType(), ACTION_DELIVER_PHYSICAL_GOODS,
                            "verify fulfilment item type");
                    verifyEqual(fulfilmentAction.getStatus(), "PENDING", "verify fulfilment status");
                } else {
                    verifyEqual(fulfilmentAction.getType(), ACTION_GRANT_ENTITLEMENT, "verify fulfilment status");
                    verifyEqual(fulfilmentAction.getStatus(), "SUCCEED", "verify fulfilment status");
                }
            }
        }
    }

    private OrderItem getOrderItemByOfferId(String offerId, Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem.getOffer().getValue().equals(offerId)) {
                return orderItem;
            }
        }
        return null;
    }

    public void validateSingleFulfilmentItem(FulfilmentItem fulfilmentItem, String orderId, boolean hasPhysicalGood) {
        Order order = Master.getInstance().getOrder(orderId);
        OrderItem orderItem = order.getOrderItems().get(0);
        verifyEqual(fulfilmentItem.getOfferId(), orderItem.getOffer().getValue(), "verify offer id");
        verifyEqual(fulfilmentItem.getQuantity(), orderItem.getQuantity(), "verify offer quantity");

        if (fulfilmentItem.getActions().size() > 0) {
            for (FulfilmentAction fulfilmentAction : fulfilmentItem.getActions()) {
                if (hasPhysicalGood) {
                    verifyEqual(fulfilmentAction.getType(), ACTION_DELIVER_PHYSICAL_GOODS,
                            "verify fulfilment item type");
                    verifyEqual(fulfilmentAction.getStatus(), "PENDING", "verify fulfilment status");
                } else {
                    verifyEqual(fulfilmentAction.getType(), ACTION_GRANT_ENTITLEMENT, "verify fulfilment status");
                    verifyEqual(fulfilmentAction.getStatus(), "SUCCEED", "verify fulfilment status");
                }
            }
        } else {
            throw new TestException("missing fulfilment action");
        }
    }


}
