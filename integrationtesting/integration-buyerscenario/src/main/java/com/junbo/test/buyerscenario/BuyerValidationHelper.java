/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.buyerscenario;

import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.ShippingAddressId;
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

    public void validateOrderInfoByCartId(String uid, String orderId,
                                          String cartId, Country country, Currency currency,
                                          String paymentInstrumentId, String shippingAddressId) {
        Order order = Master.getInstance().getOrder(orderId);
        Cart cart = Master.getInstance().getCart(cartId);

        //verifyEqual(order.getTentative(), false, "verify tentative after order complete");
        verifyEqual(order.getCountry(), country.toString(), "verify country field in order");
        verifyEqual(order.getCurrency(), currency.toString(), "verify currency field in order");
        verifyEqual(order.getStatus(), "COMPLETED", "verify order status");

        verifyEqual(order.getOrderItems().size(), cart.getOffers().size(), "verify offer items in order");
        if (shippingAddressId != null) {
            verifyEqual(IdConverter.idLongToHexString(
                    ShippingAddressId.class, order.getShippingAddressId().getValue()), shippingAddressId,
                    "verify shipping address id"
            );
        }
        verifyEqual(
                IdConverter.idLongToHexString(
                        PaymentInstrumentId.class, order.getPaymentInstruments().get(0).getValue()),
                paymentInstrumentId,
                "verify payment instrument id"
        );

        BigDecimal expectedTotalAmount = new BigDecimal(0);
        BigDecimal expectedTotalTaxAmount = new BigDecimal(0);
        for (int i = 0; i < cart.getOffers().size(); i++) {
            OfferItem offerItem = cart.getOffers().get(i);
            for (OrderItem orderItem : order.getOrderItems()) {
                if (offerItem.getOffer().equals(orderItem.getOffer())) {
                    verifyEqual(orderItem.getQuantity(), Integer.valueOf(
                            offerItem.getQuantity().toString()), "verify offer quantity");
                    BigDecimal unitPrice = Master.getInstance().getOffer(
                            IdConverter.idToHexString(offerItem.getOffer()))
                            .getPrices().get(country.toString()).getAmount();
                    BigDecimal expectedOrderItemAmount = unitPrice.multiply(new BigDecimal(offerItem.getQuantity()));
                    verifyEqual(orderItem.getTotalAmount().toString(),
                            expectedOrderItemAmount.toString(), "verify order item amount");
                    BigDecimal expectedTaxUpper = expectedOrderItemAmount.multiply(new BigDecimal(0.089)).
                            setScale(2, RoundingMode.UP);
                    BigDecimal expectedTaxLower = expectedOrderItemAmount.multiply(new BigDecimal(0.085)).
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

        verifyEqual(order.getTotalAmount(), expectedTotalAmount, "verify order total amount");
        verifyEqual(order.getTotalTax(), expectedTotalTaxAmount, "verify order total tax");
    }

    public void validateEmailHistory(String uid, String orderId) throws Exception {
        String id = IdConverter.hexStringToId(UserId.class, uid).toString();
        String sql = String.format("select payload from email_history where user_id=\'%s\'", id);
        String resultString = dbHelper.executeScalar(sql, DBHelper.DBName.EMAIL);

        verifyEqual(resultString.indexOf("OrderConfirmation") >= 0, true, "Verify email type");
        verifyEqual(resultString.indexOf(
                IdConverter.hexStringToId(OrderId.class, orderId).toString()) >= 0, true, "verify order Id");
        verifyEqual(resultString.indexOf("SUCCEED") >= 0, true, "Verify email sent status");
        verifyEqual(resultString.indexOf(
                Master.getInstance().getUser(uid).getUserName()) >= 0, true, "verify email receipt correct");
        verifyEqual(resultString.indexOf(
                IdConverter.hexStringToId(UserId.class, uid).toString()) >= 0, true, "verify user id");

    }

}
