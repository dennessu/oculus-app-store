/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.buyerscenario;

import com.junbo.cart.spec.model.item.OfferItem;

import com.junbo.common.id.OfferRevisionId;
//import com.junbo.common.id.ShippingAddressId;
import com.junbo.common.id.UserId;
import com.junbo.order.spec.model.OrderItem;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Utility.BaseValidationHelper;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.DBHelper;
import com.junbo.order.spec.model.Order;
import com.junbo.cart.spec.model.Cart;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.ShardIdHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Yunlong on 3/27/14.
 */
public class BuyerValidationHelper extends BaseValidationHelper {
    DBHelper dbHelper = new DBHelper();
    BuyerTestDataProvider testDataProvider = new BuyerTestDataProvider();

    public BuyerValidationHelper() {
        super();
    }

    public void validateEwalletBalance(String uid, String orderId) throws Exception{
        Order order=  Master.getInstance().getOrder(orderId);
        BigDecimal totalAmount = order.getTotalAmount().add(order.getTotalTax());
        String sqlStr = String.format(
                "select balance from shard_%s.ewallet where user_id = '%s'",
                ShardIdHelper.getShardIdByUid(uid), IdConverter.hexStringToId(UserId.class,uid));
        verifyEqual(dbHelper.executeScalar(sqlStr, DBHelper.DBName.EWALLET),
                new BigDecimal(500).subtract(totalAmount).toString(), "verify ewallet balance");
    }

    public void validateOrderInfoByCartId(String uid, String orderId, String cartId, Country country, Currency currency,
                                          String paymentInstrumentId, String shippingAddressId) {
        validateOrderInfoByCartId(uid, orderId, cartId, country, currency, paymentInstrumentId,
                shippingAddressId, false);
    }

    public void validateOrderInfoByCartId(String uid, String orderId, String cartId, Country country, Currency currency,
                                          String paymentInstrumentId, String shippingAddressId, boolean hasPhysicalGood) {
        Order order = Master.getInstance().getOrder(orderId);
        Cart cart = Master.getInstance().getCart(cartId);

        verifyEqual(order.getTentative(), false, "verify tentative after order complete");
        verifyEqual(order.getCountry().toString(), country.toString(), "verify country field in order");
        verifyEqual(order.getCurrency().toString(), currency.toString(), "verify currency field in order");

        if (hasPhysicalGood) {
            verifyEqual(order.getStatus(), "PENDING_FULFILL", "verify order status");
        } else {
            verifyEqual(order.getStatus(), "COMPLETED", "verify order status");
        }

        verifyEqual(order.getOrderItems().size(), cart.getOffers().size(), "verify offer items in order");
        /*if (shippingAddressId != null) {
            verifyEqual(IdConverter.idLongToHexString(
                    ShippingAddressId.class, order.getShippingAddress().getValue()), shippingAddressId,
                    "verify shipping address id"
            );
        }*/

        BigDecimal expectedTotalAmount = new BigDecimal(0.00);
        BigDecimal expectedTotalTaxAmount = new BigDecimal(0.00);
        for (int i = 0; i < cart.getOffers().size(); i++) {
            OfferItem offerItem = cart.getOffers().get(i);
            for (OrderItem orderItem : order.getOrderItems()) {
                if (offerItem.getOffer().equals(orderItem.getOffer())) {
                    verifyEqual(orderItem.getQuantity(), Integer.valueOf(
                            offerItem.getQuantity().toString()), "verify offer quantity");
                    String offerId = IdConverter.idToHexString(offerItem.getOffer());

                    String currentOfferRevisionId = IdConverter.idLongToHexString(OfferRevisionId.class,
                            Master.getInstance().getOffer(offerId).getCurrentRevisionId());
                    BigDecimal unitPrice = Master.getInstance().getOfferRevision(currentOfferRevisionId).getPrice()
                            .getPrices().get(currency.toString());

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
                    break;
                }
            }
        }

        verifyEqual(order.getTotalAmount(), expectedTotalAmount.setScale(2), "verify order total amount");
        verifyEqual(order.getTotalTax(), expectedTotalTaxAmount.setScale(2), "verify order total tax");

    }

    public void validateEmailHistory(String uid, String orderId) throws Exception {
        String id = IdConverter.hexStringToId(UserId.class, uid).toString();
        String sql = String.format("select payload from shard_%s.email_history where user_id=\'%s\'",
                ShardIdHelper.getShardIdByUid(uid), id);
        String resultString = dbHelper.executeScalar(sql, DBHelper.DBName.EMAIL);

        //verifyEqual(resultString.indexOf("OrderConfirmation") >= 0, true, "Verify email type");
        verifyEqual(resultString.indexOf(orderId) >= 0, true, "verify order Id");
        verifyEqual(resultString.indexOf("SUCCEED") >= 0, true, "Verify email sent status");
        verifyEqual(resultString.indexOf(
                Master.getInstance().getUser(uid).getUsername()) >= 0, true, "verify email receipt correct");
        verifyEqual(resultString.indexOf(
                IdConverter.hexStringToId(UserId.class, uid).toString()) >= 0, true, "verify user id");

    }

}
