/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Utility;

import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.ShippingAddressId;
import com.junbo.order.spec.model.OrderItem;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.DBHelper;
import com.junbo.order.spec.model.Order;
import com.junbo.cart.spec.model.Cart;
import com.junbo.test.common.libs.IdConverter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Yunlong on 3/27/14.
 */
public class ValidationHelper {
    DBHelper dbHelper = new DBHelper();
    TestDataProvider testDataProvider = new TestDataProvider();

    public ValidationHelper() {

    }

    public void validateOrderInfoByCartId(String uid, String orderId,
                                          String cartId, Country country, Currency currency,
                                          String paymentInstrumentId, String shippingAddressId) {
        Order order = Master.getInstance().getOrder(orderId);
        Cart cart = Master.getInstance().getCart(cartId);

        //verifyEqual(order.getTentative(), false, "verify tentative after order complete");
        verifyEqual(order.getCountry(), country.toString(), "verify country field in order");
        verifyEqual(order.getCurrency(), currency.toString(), "verify currency field in order");
        verifyEqual(order.getStatus(), "OPEN", "verify order status");

        verifyEqual(order.getOrderItems().size(), cart.getOffers().size(), "verify offer items in order");
        if (shippingAddressId != null) {
            verifyEqual(IdConverter.idLongToHexString(
                    ShippingAddressId.class, order.getShippingAddressId().getValue()), shippingAddressId,
                    "verify shipping address id");
        }
        verifyEqual(IdConverter.idLongToHexString(
                PaymentInstrumentId.class, order.getPaymentInstruments().get(0).getValue()), paymentInstrumentId,
                "verify payment instrument id");

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
                    verifyEqual(orderItem.getTotalTax(),
                            expectedOrderItemAmount.multiply(new BigDecimal(0.87)), "Verify total tax ");
                    expectedTotalTaxAmount.add(orderItem.getTotalTax());
                    expectedTotalAmount.add(expectedOrderItemAmount);
                    break;
                }
            }
        }

        verifyEqual(order.getTotalAmount(), expectedTotalAmount, "verify order total amount");
        verifyEqual(order.getTotalTax(), expectedTotalTaxAmount, "verify order total tax");
    }

    public void validateEmailHistory(String uid) throws Exception {
        String sql = "";
        verifyEqual(dbHelper.executeScalar(sql), "", "");
    }

    public static void verifyEqual(BigDecimal actual, BigDecimal expect, String message) {
        if (actual != expect) {
            throw new TestException(
                    String.format("Verify failed for %s, expect %s, but found %s", message, expect, actual));
        }
    }


    public static void verifyEqual(int actual, int expect, String message) {
        if (actual != expect) {
            throw new TestException(
                    String.format("Verify failed for %s, expect %s, but found %s", message, expect, actual));
        }
    }

    public static void verifyEqual(long actual, long expect, String message) {
        if (actual != expect) {
            throw new TestException(
                    String.format("Verify failed for %s, expect %s, but found %s", message, expect, actual));
        }
    }

    public static void verifyEqual(boolean actual, boolean expect, String message) {
        if (actual != expect) {
            throw new TestException(
                    String.format("Verify failed for %s, expect %s, but found %s", message, expect, actual));
        }
    }

    public static void verifyEqual(List<String> actual, List<String> expect, String message) {
        if (actual.size() != expect.size() || !(actual.equals(expect))) {
            throw new TestException(
                    String.format("Verify failed for %s, expect %s, but found %s", message, expect, actual));
        }
    }

    public static void verifyEqual(String actual, String expect, String message) {
        verifyEqual(actual, expect, true, true, message);
    }

    public static void verifyEqual(
            String actual, String expect, boolean ignoreCase, boolean ignoreWhitespace, String message) {
        boolean sameString = false;
        if (ignoreCase && !ignoreWhitespace) {
            sameString = expect.equalsIgnoreCase(actual);
        } else if (!ignoreCase & ignoreWhitespace) {
            sameString = (expect.trim()).equals((actual.trim()));
        } else if (ignoreCase & ignoreWhitespace) {
            sameString = (expect.trim()).equalsIgnoreCase((actual.trim()));
        } else if (!ignoreCase & !ignoreWhitespace) {
            sameString = expect.equals(actual);
        }

        if (!sameString) {
            throw new TestException(
                    String.format("Verify failed for %s, expect %s, but found %s", message, expect, actual));
        }
    }


}
