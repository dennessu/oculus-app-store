/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.endtoend;

import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.common.model.Results;
import com.junbo.email.spec.model.EmailStatus;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import com.junbo.identity.spec.v1.model.UserLoginName;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.order.spec.model.FulfillmentHistory;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderItem;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Utility.ValidationHelper;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.DBHelper;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.ShardIdHelper;
import com.junbo.test.identity.Identity;
import com.junbo.fulfilment.spec.model.FulfilmentAction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by Yunlong on 3/27/14.
 */
public class BuyerValidationHelper extends ValidationHelper {
    DBHelper dbHelper = new DBHelper();
    BuyerTestDataProvider testDataProvider = new BuyerTestDataProvider();

    public BuyerValidationHelper() {
        super();
    }

    public void validateEwalletBalance(String uid, String orderId) throws Exception {
        Order order = Master.getInstance().getOrder(orderId);
        BigDecimal totalAmount = order.getTotalAmount().add(order.getTotalTax());
        String sqlStr = String.format(
                "select balance from shard_%s.ewallet where user_id = '%s'",
                ShardIdHelper.getShardIdByUid(uid), IdConverter.hexStringToId(UserId.class, uid));
        verifyEqual(dbHelper.executeScalar(sqlStr, DBHelper.DBName.EWALLET),
                new BigDecimal(1000).subtract(totalAmount).toString(), "verify ewallet balance");
    }

    public void validateOrderInfoByCartId(String uid, String orderId, String cartId, Country country, Currency currency,
                                          String paymentInstrumentId) throws Exception {
        validateOrderInfoByCartId(uid, orderId, cartId, country, currency, paymentInstrumentId, false);
    }

    public void validateFreeOrderInfo(String uid, String orderId, Country country, Currency currency, boolean hasPhysicalGood) throws Exception {
        Results<Entitlement> entitlementResults = testDataProvider.getEntitlementByUserId(uid);

        validateEntitlements(entitlementResults, 2);
        testDataProvider.getOrder(orderId);
        Order order = Master.getInstance().getOrder(orderId);

        if (hasPhysicalGood) {
            verifyEqual(order.getStatus(), "PENDING", "verify order status");
        } else {
            verifyEqual(order.getStatus(), "COMPLETED", "verify order status");
        }

        verifyEqual(order.getTentative(), false, "verify order tentative");
        verifyEqual(order.getCurrency().getValue(), currency.toString(), "verify order currency");
        verifyEqual(order.getCountry().getValue(), country.toString(), "verify order country");
        verifyEqual(order.getLocale().getValue(), "en_US", "verify locale");
        verifyEqual(order.getTotalAmount(), new BigDecimal(0), "verify total amount");
        verifyEqual(order.getTotalTax(), new BigDecimal(0), "verify total tax");
        FulfillmentHistory fulfilmentHistory = order.getOrderItems().get(0).getFulfillmentHistories().get(0);
        verifyEqual(fulfilmentHistory.getSuccess(), true, "verify fulfilment status");
        if (hasPhysicalGood) {
            verifyEqual(fulfilmentHistory.getFulfillmentEvent().toString(), "REQUEST_SHIP", "verify fulfillment event");
        } else {
            verifyEqual(fulfilmentHistory.getFulfillmentEvent().toString(), "FULFILL", "verify fulfillment event");
        }
        if (entitlementResults.getItems().size() > 0) {
            verifyEqual(fulfilmentHistory.getEntitlements().size(), entitlementResults.getItems().size(), "verify entitlement size");
        } else if (!fulfilmentHistory.getEntitlements().isEmpty()) {
            throw new TestException("entitlement should be null");
        }

    }

    public void validateOrderInfoByCartId(String uid, String orderId, String cartId, Country country, Currency currency,
                                          String paymentInstrumentId, boolean hasPhysicalGood) throws Exception {
        testDataProvider.getOrder(orderId);
        Order order = Master.getInstance().getOrder(orderId);
        Cart cart = Master.getInstance().getCart(cartId);
        String fulfilmentId = testDataProvider.getFulfilmentsByOrderId(orderId);
        //String balanceId = testDataProvider.getBalancesByOrderId(orderId).get(0);
        verifyEqual(order.getTentative(), false, "verify tentative after order complete");
        verifyEqual(order.getCountry().toString(), country.toString(), "verify country field in order");
        verifyEqual(order.getCurrency().toString(), currency.toString(), "verify currency field in order");

        if (hasPhysicalGood) {
            verifyEqual(order.getStatus(), "PENDING", "verify order status");
        } else {
            verifyEqual(order.getStatus(), "COMPLETED", "verify order status");
        }

        verifyEqual(order.getOrderItems().size(), cart.getOffers().size(), "verify offer items in order");
        verifyEqual(order.getPayments().get(0).getPaymentInstrument().getValue(),
                IdConverter.hexStringToId(PaymentInstrumentId.class, paymentInstrumentId),
                "verify payment instrument id");

        if (hasPhysicalGood) {
            verifyEqual(order.getShippingAddress().getValue(),
                    Master.getInstance().getUser(uid).getAddresses().get(0).getValue().getValue(),
                    "verify personal info address id"
            );
        }

        BigDecimal expectedTotalAmount = new BigDecimal(0.00);
        BigDecimal expectedTotalTaxAmount = new BigDecimal(0.00);
        for (int i = 0; i < cart.getOffers().size(); i++) {
            OfferItem offerItem = cart.getOffers().get(i);
            for (OrderItem orderItem : order.getOrderItems()) {
                if (offerItem.getOffer().equals(orderItem.getOffer())) {
                    verifyEqual(orderItem.getQuantity(), Integer.valueOf(
                            offerItem.getQuantity().toString()), "verify offer quantity");
                    String offerId = IdConverter.idToHexString(offerItem.getOffer());

                    String currentOfferRevisionId = IdConverter.idToUrlString(OfferRevisionId.class,
                            Master.getInstance().getOffer(offerId).getCurrentRevisionId());
                    BigDecimal unitPrice = Master.getInstance().getOfferRevision(currentOfferRevisionId).getPrice()
                            .getPrices().get(country.toString()).get(currency.toString());

                    BigDecimal expectedOrderItemAmount = unitPrice.multiply(
                            new BigDecimal(offerItem.getQuantity())).setScale(2);
                    verifyEqual(orderItem.getTotalAmount().toString(),
                            expectedOrderItemAmount.toString(), "verify order item amount");
                    BigDecimal expectedTaxUpper = expectedOrderItemAmount.multiply(new BigDecimal(0.090)).
                            setScale(2, RoundingMode.UP);
                    BigDecimal expectedTaxLower = expectedOrderItemAmount.multiply(new BigDecimal(0.075)).
                            setScale(2, RoundingMode.UP);

                    if (orderItem.getTotalTax().compareTo(expectedTaxUpper) > 0 ||
                            orderItem.getTotalTax().compareTo(expectedTaxLower) < 0) {
                        throw new TestException("verify tax failed");
                    }

                    expectedTotalTaxAmount = expectedTotalTaxAmount.add(orderItem.getTotalTax());
                    expectedTotalAmount = expectedTotalAmount.add(expectedOrderItemAmount);

                    validateFulfilmentHistory(orderItem, getFulfilmentItemByOfferId(fulfilmentId, offerId));
                    break;
                }
            }
        }

        verifyEqual(order.getTotalAmount(), expectedTotalAmount.setScale(2), "verify order total amount");
        verifyEqual(order.getTotalTax(), expectedTotalTaxAmount.setScale(2), "verify order total tax");

    }

    public void validateBillingHistory(Order order) {
        //TODO Pending billing cloudant bug fixed

    }

    private void validateFulfilmentHistory(OrderItem orderItem, FulfilmentItem fulfilmentItem) {
        List<FulfillmentHistory> fulfillmentHistories = orderItem.getFulfillmentHistories();
        List<FulfilmentAction> fulfillmentActions = fulfilmentItem.getActions();

        for (FulfilmentAction fulfilmentAction : fulfillmentActions) {
            if (fulfilmentAction.getType().equals("DELIVER_PHYSICAL_GOODS")) {
                for (FulfillmentHistory fulfillmentHistory : fulfillmentHistories) {
                    verifyEqual(fulfillmentHistory.getFulfillmentEvent(), "REQUEST_SHIP", "verify fulfilment history");
                    verifyEqual(fulfillmentHistory.getSuccess(), true, "verify fulfil status");
                }
            } else if (fulfilmentAction.getType().equals("GRANT_ENTITLEMENT")) {
                for (FulfillmentHistory fulfillmentHistory : fulfillmentHistories) {
                    verifyEqual(fulfillmentHistory.getFulfillmentEvent(), "FULFILL", "verify fulfilment history");
                    verifyEqual(fulfillmentHistory.getSuccess(), true, "verify fulfil status");
                }
            }
        }

    }

    private FulfilmentItem getFulfilmentItemByOfferId(String fulfilmentId, String offerId) throws Exception {
        FulfilmentRequest fulfilment = Master.getInstance().getFulfilment(fulfilmentId);
        List<FulfilmentItem> fulfilmentItems = fulfilment.getItems();
        for (FulfilmentItem fulfilmentItem : fulfilmentItems) {
            if (fulfilmentItem.getOfferId().equals(offerId)) {
                return fulfilmentItem;
            }
        }

        throw new TestException("Can not find specific offer id in fulfilment item");
    }

    public void validateEntitlements(Results<Entitlement> entitlementResults, int expectedCount) throws Exception{
        List<Entitlement> entitlements = entitlementResults.getItems();
        for (int i = 0; i < entitlements.size(); i++) {
            Entitlement entitlement = entitlements.get(i);
            testDataProvider.getBinariesUrl(entitlement);
            verifyEqual(true, entitlement.getIsActive(), "verify entitlement active is true");
            verifyEqual(false, entitlement.getIsBanned(), "verify entilement banned is false");
        }

    }


    public void validateEmailHistory(String uid, String orderId) throws Exception {
        String id = IdConverter.hexStringToId(UserId.class, uid).toString();
        String sql = String.format("select payload from shard_%s.email_history where user_id=\'%s\'",
                ShardIdHelper.getShardIdByUid(uid), id);
        String resultPayload = dbHelper.executeScalar(sql, DBHelper.DBName.EMAIL);

        Thread.sleep(5000);
        sql = String.format("select status from shard_%s.email_history where user_id=\'%s\'",
                ShardIdHelper.getShardIdByUid(uid), id);
        String resultStatus = dbHelper.executeScalar(sql, DBHelper.DBName.EMAIL);

        Long orderIdLong = IdConverter.hexStringToId(OrderId.class, orderId);
        //verify payload
        verifyEqual(resultPayload.indexOf(orderIdLong.toString()) >= 0, true, "verify order Id");
        UserPersonalInfo personalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(Master.getInstance().getUser(uid).getUsername());
        UserLoginName loginName = ObjectMapperProvider.instance().treeToValue(personalInfo.getValue(), UserLoginName.class);
        verifyEqual(resultPayload.indexOf(loginName.getUserName()) >= 0, true, "verify email receipt correct");
        verifyEqual(resultPayload.indexOf(
                IdConverter.hexStringToId(UserId.class, uid).toString()) >= 0, true, "verify user id");

        //verify email send status: 1: pending 2: succeed 3: fail 4: SCHEDULED
        verifyEqual(resultStatus, EmailStatus.SUCCEED.getId().toString(), "email send status");
    }

}
