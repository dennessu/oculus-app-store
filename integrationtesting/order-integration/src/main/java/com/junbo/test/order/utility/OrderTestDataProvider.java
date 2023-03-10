/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.test.order.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.billing.spec.model.BalanceItem;
import com.junbo.billing.spec.model.TaxItem;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.*;
import com.junbo.common.model.Results;
import com.junbo.order.spec.model.*;
import com.junbo.order.spec.model.PaymentInfo;
import com.junbo.test.billing.utility.BillingTestDataProvider;
import com.junbo.test.catalog.OfferRevisionService;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.impl.OfferRevisionServiceImpl;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.PaymentInstrumentBase;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.order.apihelper.OrderEventService;
import com.junbo.test.order.apihelper.OrderService;
import com.junbo.test.order.apihelper.impl.OrderEventServiceImpl;
import com.junbo.test.order.apihelper.impl.OrderServiceImpl;
import com.junbo.test.order.model.*;
import com.junbo.test.order.model.enums.BillingAction;
import com.junbo.test.order.model.enums.EventStatus;
import com.junbo.test.order.model.enums.OrderActionType;
import com.junbo.test.order.model.enums.OrderStatus;
import com.junbo.test.payment.utility.PaymentTestDataProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by weiyu_000 on 5/19/14.
 */
public class OrderTestDataProvider {
    protected OrderService orderClient = OrderServiceImpl.getInstance();
    protected OrderEventService orderEventClient = OrderEventServiceImpl.getInstance();
    protected OfferService offerClient = OfferServiceImpl.instance();
    protected OfferRevisionService offerRevisionClient = OfferRevisionServiceImpl.instance();
    protected UserService identityClient = UserServiceImpl.instance();
    protected PaymentTestDataProvider paymentProvider = new PaymentTestDataProvider();
    protected BillingTestDataProvider billingProvider = new BillingTestDataProvider();

    private final String payerId = "CCZA9BJT9NKTS";


    public String createUser() throws Exception {
        return identityClient.PostUser();
    }

    public String postPaymentInstrument(String uid, PaymentInstrumentBase paymentInfo) throws Exception {
        return paymentProvider.postPaymentInstrument(uid, paymentInfo);
    }

    public void creditWallet(String uid, BigDecimal amount) throws Exception {
        paymentProvider.creditWallet(uid, amount);
    }

    public void updateOfferPrice(String offerName) throws Exception {
        String offerId = offerClient.getOfferIdByName(offerName);

        Offer offer = Master.getInstance().getOffer(offerId);

        String offerRevisionId = IdConverter.idToUrlString(OfferRevisionId.class,
                offer.getCurrentRevisionId());

        OfferRevision offerRevision = Master.getInstance().getOfferRevision(offerRevisionId);
        Price price = offerRevision.getPrice();
        Map<String, Map<String, BigDecimal>> originPrices = price.getPrices();
        BigDecimal currentPrice = originPrices.get(Country.DEFAULT.toString()).get(Currency.DEFAULT.toString());
        if (currentPrice.compareTo(new BigDecimal(10)) >= 0) {
            originPrices.remove(Country.DEFAULT.toString());
            Map<String, BigDecimal> currencyMap = new HashMap<>();
            currencyMap.put(Currency.DEFAULT.toString(), new BigDecimal(9));
            originPrices.put(Country.DEFAULT.toString(), currencyMap);
        } else {
            originPrices.remove(Currency.DEFAULT.toString());
            Map<String, BigDecimal> currencyMap = new HashMap<>();
            currencyMap.put(Currency.DEFAULT.toString(), new BigDecimal(11));
            originPrices.put(Country.DEFAULT.toString(), currencyMap);
        }
        price.setPrices(originPrices);
        offerRevision.setId(null);
        offerRevision.setResourceAge(null);
        offerRevision.setStatus("DRAFT");
        offerRevision.setRev(null);
        OfferRevision offerRevisionUpdated = offerRevisionClient.postOfferRevision(offerRevision);
        offerRevisionUpdated.setStatus("APPROVED");
        offerRevisionClient.updateOfferRevision(offerRevisionUpdated.getRevisionId(), offerRevisionUpdated);
    }

    public String postOrder(String uid, Country country, Currency currency, String paymentInstrumentId,
                            boolean hasPhysicalGood, Map<String, Integer> offers)
            throws Exception {
        return postOrder(uid, country, currency, paymentInstrumentId, hasPhysicalGood, offers, 200, true);
    }

    public String postOrder(String uid, Country country, Currency currency, String paymentInstrumentId,
                            boolean hasPhysicalGood, Map<String, Integer> offers, boolean tentative)
            throws Exception {
        return postOrder(uid, country, currency, paymentInstrumentId, hasPhysicalGood, offers, 200, tentative);
    }

    public String postOrder(String uid, Country country, Currency currency, String paymentInstrumentId,
                            boolean hasPhysicalGood, Map<String, Integer> offers, int expectedResponseCode)
            throws Exception {
        return postOrder(uid, country, currency, paymentInstrumentId, hasPhysicalGood, offers, expectedResponseCode, true);
    }

    public String postOrder(String uid, Country country, Currency currency, String paymentInstrumentId,
                            boolean hasPhysicalGood, Map<String, Integer> offers, int expectedResponseCode, boolean tentative)
            throws Exception {
        Order order = new Order();
        UserId userId = new UserId();
        userId.setValue(IdConverter.hexStringToId(UserId.class, uid));
        order.setUser(userId);
        order.setCountry(new CountryId(country.toString()));
        order.setCurrency(new CurrencyId(currency.toString()));
        List<PaymentInfo> paymentInfos = new ArrayList<>();
        PaymentInfo paymentInfo = new PaymentInfo();

        if (paymentInstrumentId != null) {
            if (paymentInstrumentId.equals("Invalid")) {
                paymentInfo.setPaymentInstrument(new PaymentInstrumentId(0L));
            } else {
                paymentInfo.setPaymentInstrument(new PaymentInstrumentId(
                        IdConverter.hexStringToId(PaymentInstrumentId.class, paymentInstrumentId)));
            }
            paymentInfos.add(paymentInfo);
        }
        order.setPayments(paymentInfos);
        order.setShippingMethod("0");

        if (hasPhysicalGood) {
            order.setShippingMethod("0");
            order.setShippingAddress(Master.getInstance().getUser(uid).getAddresses().get(0).getValue());
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        Set<String> key = offers.keySet();
        for (Iterator it = key.iterator(); it.hasNext(); ) {
            OrderItem orderItem = new OrderItem();
            String offerName = (String) it.next();
            OfferId offerId;
            if (offerName.equals("Invalid")) {
                offerId = new OfferId(uid);
            } else if (offerName.toLowerCase().contains("test")) {
                offerId = new OfferId(offerClient.getOfferIdByName(offerName));
            } else {
                offerId = new OfferId(offerName);
            }
            orderItem.setQuantity(offers.get(offerName));
            orderItem.setOffer(offerId);
            orderItemList.add(orderItem);

        }
        order.setOrderItems(orderItemList);
        order.setTentative(tentative);
        order.setLocale(new LocaleId("en_US"));
        Master.getInstance().setCurrentUid(uid);
        return orderClient.postOrder(order, expectedResponseCode);
    }

    public String postOrder(String uid, Country country, Currency currency, String paymentInstrumentId,
                            boolean hasPhysicalGood, long shippingAddressId, Map<String, Integer> offers,
                            int expectedResponseCode) throws Exception {
        Order order = new Order();
        order.setUser(new UserId(IdConverter.hexStringToId(UserId.class, uid)));
        order.setCountry(new CountryId(country.toString()));
        order.setCurrency(new CurrencyId(currency.toString()));
        List<PaymentInfo> paymentInfos = new ArrayList<>();
        PaymentInfo paymentInfo = new PaymentInfo();

        if (paymentInstrumentId.equals("Invalid")) {
            paymentInfo.setPaymentInstrument(new PaymentInstrumentId(0L));
        } else {
            paymentInfo.setPaymentInstrument(new PaymentInstrumentId(
                    IdConverter.hexStringToId(PaymentInstrumentId.class, paymentInstrumentId)));
        }
        paymentInfos.add(paymentInfo);
        order.setPayments(paymentInfos);
        order.setShippingMethod("0");

        if (hasPhysicalGood) {
            order.setShippingMethod("0");
            if (shippingAddressId <= 0) {
                order.setShippingAddress(Master.getInstance().getUser(uid).getAddresses().get(0).getValue());
            } else {
                order.setShippingAddress(new UserPersonalInfoId(shippingAddressId));
            }
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        Set<String> key = offers.keySet();
        for (Iterator it = key.iterator(); it.hasNext(); ) {
            OrderItem orderItem = new OrderItem();
            String offerName = (String) it.next();
            OfferId offerId;
            if (offerName.equals("Invalid")) {
                offerId = new OfferId("123");
            } else {
                offerId = new OfferId(offerClient.getOfferIdByName(offerName));
            }
            orderItem.setQuantity(offers.get(offerName));
            orderItem.setOffer(offerId);
            orderItemList.add(orderItem);

        }
        order.setOrderItems(orderItemList);
        order.setTentative(true);
        order.setLocale(new LocaleId("en_US"));
        Master.getInstance().setCurrentUid(uid);
        return orderClient.postOrder(order, expectedResponseCode);
    }


    public String updateOrderTentative(String orderId, boolean isTentative, int expectedResponseCode) throws Exception {
        Order order = Master.getInstance().getOrder(orderClient.getOrderByOrderId(orderId));
        order.setTentative(isTentative);
        return orderClient.updateOrder(order, expectedResponseCode);
    }

    public String updateOrderTentative(String orderId, boolean isTentative) throws Exception {

        return updateOrderTentative(orderId, isTentative, 200);
    }

    public List<String> getOrdersByUserId(String uid) throws Exception {
        return orderClient.getOrdersByUserId(uid);
    }

    public String updateOrder(Order order) throws Exception {
        return orderClient.updateOrder(order);
    }

    public String getOrder(String orderId) throws Exception {
        return orderClient.getOrderByOrderId(orderId);
    }

    public String getOrder(String orderId, int expectedResponseCode) throws Exception {
        return orderClient.getOrderByOrderId(orderId, expectedResponseCode);
    }

    public String postOrder(Order order) throws Exception {
        return orderClient.postOrder(order);
    }

    public String postOrder(Order order, int expectedResponseCode) throws Exception {
        return orderClient.postOrder(order, expectedResponseCode);
    }

    public Results<Subledger> getSubledger(String offerName) throws Exception {
        String offerId = offerClient.getOfferIdByName(offerName);
        Offer offer = Master.getInstance().getOffer(offerId);
        String offerRevisionId = IdConverter.idToUrlString(OfferRevisionId.class, offer.getCurrentRevisionId());
        String sellerId = IdConverter.idToUrlString(OrganizationId.class,
                Master.getInstance().getOfferRevision(offerRevisionId).getOwnerId().getValue());
        return orderClient.getSubledgers(sellerId);
    }

    public void invalidateCreditCard(String uid, String paymentId) throws Exception {
        paymentProvider.invalidateCreditCard(uid, paymentId);
    }

    public Results<OrderEvent> getOrderEventsByOrderId(String orderId) throws Exception {
        return orderEventClient.getOrderEventsByOrderId(orderId);
    }

    public OrderEvent getOrderEvents(String id, int expectedResponseCode) throws Exception {
        return orderEventClient.getOrderEvent(id, expectedResponseCode);
    }

    public void postOrderEvent(String orderId, EventStatus eventStatus, OrderActionType orderActionType, int expectedResponseCode)
            throws Exception {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrder(new OrderId(IdConverter.hexStringToId(OrderId.class, orderId)));
        orderEvent.setAction(orderActionType.toString());
        orderEvent.setStatus(eventStatus.toString());
        orderEventClient.postOrderEvent(orderEvent, expectedResponseCode);
    }

    public void postOrderEvent(String orderId, EventStatus eventStatus, OrderActionType orderActionType)
            throws Exception {
        postOrderEvent(orderId, eventStatus, orderActionType, 200);
    }

    public OrderInfo getRefundedOrderInfo(OrderInfo orderInfo, Map<String, Integer> refundedOffers,
                                          Map<String, BigDecimal> partialRefundAmounts) throws Exception {

        BillingHistoryInfo billingHistory = new BillingHistoryInfo();

        BigDecimal orderTotalRefundedAmount = new BigDecimal(0);
        BigDecimal orderTotalRefundedTax = new BigDecimal(0);

        for (int i = 0; i < orderInfo.getOrderItems().size(); i++) {
            String offerId = orderInfo.getOrderItems().get(i).getOfferId();

            String currentOfferRevisionId = IdConverter.idToUrlString(OfferRevisionId.class,
                    Master.getInstance().getOffer(offerId).getCurrentRevisionId());

            String offerName = Master.getInstance().getOfferRevision(currentOfferRevisionId)
                    .getLocales().get(orderInfo.getLocale()).getName();
            BigDecimal unitPrice = Master.getInstance().getOfferRevision(currentOfferRevisionId).getPrice()
                    .getPrices().get(orderInfo.getCountry().toString()).get(orderInfo.getCurrency().toString());

            int refundQuantity = 0;
            if (refundedOffers != null && refundedOffers.containsKey(offerName)) {
                refundQuantity = refundedOffers.get(offerName);
            }
            OrderItemInfo orderItem = orderInfo.getOrderItems().get(i);
            RefundOrderItemInfo refundOrderItem = new RefundOrderItemInfo();
            BigDecimal refundAmount = unitPrice.multiply(
                    new BigDecimal(refundQuantity)).setScale(2, RoundingMode.HALF_UP);

            if (partialRefundAmounts != null && partialRefundAmounts.containsKey(offerName)) {
                refundAmount = refundAmount.add(partialRefundAmounts.get(offerName));
            }

            BigDecimal refundTax = refundAmount.multiply(
                    orderInfo.getTaxRate()).setScale(2, RoundingMode.HALF_UP);

            orderTotalRefundedAmount = orderTotalRefundedAmount.add(refundAmount);
            orderTotalRefundedTax = orderTotalRefundedTax.add(refundTax);

            orderInfo.getOrderItems().get(i).setOfferId(offerId);
            orderInfo.getOrderItems().get(i).setTotalAmount(orderItem.getTotalAmount().subtract(refundAmount));
            orderInfo.getOrderItems().get(i).setTotalTax(orderItem.getTotalTax().subtract(refundTax));
            orderInfo.getOrderItems().get(i).setQuantity(orderItem.getQuantity() - refundQuantity);

            refundOrderItem.setOfferId(offerId);
            refundOrderItem.setQuantity(refundQuantity);
            refundOrderItem.setRefundAmount(refundAmount.multiply(
                    new BigDecimal(-1)).setScale(2, RoundingMode.HALF_UP));
            refundOrderItem.setRefundTax(refundTax.multiply(
                    new BigDecimal(-1)).setScale(2, RoundingMode.HALF_UP));
            if (refundOrderItem.getRefundAmount().compareTo(new BigDecimal(0)) == 0) {
                continue;
            }
            billingHistory.getRefundOrderItemInfos().add(refundOrderItem);
        }

        orderInfo.setTotalAmount(orderInfo.getTotalAmount().subtract(orderTotalRefundedAmount));
        orderInfo.setTotalTax(orderInfo.getTotalTax().subtract(orderTotalRefundedTax));


        BigDecimal totalRefundAmount = orderTotalRefundedAmount.add(orderTotalRefundedTax);
        PaymentInstrumentInfo paymentInstrumentInfo = new PaymentInstrumentInfo();
        paymentInstrumentInfo.setPaymentId(orderInfo.getPaymentInfos().get(0).getPaymentId());
        paymentInstrumentInfo.setPaymentAmount(totalRefundAmount.multiply(new BigDecimal(-1)));
        billingHistory.getPaymentInfos().add(paymentInstrumentInfo);
        billingHistory.setBillingAction(BillingAction.REQUEST_REFUND);
        billingHistory.setSuccess(true);
        billingHistory.setTotalAmount(totalRefundAmount.multiply(
                new BigDecimal(-1)).setScale(2, RoundingMode.HALF_UP));
        orderInfo.getBillingHistories().add(billingHistory);

        /*
        billingHistory.setBillingAction(BillingAction.REFUND);
        orderInfo.getBillingHistories().add(billingHistory);
        */

        orderInfo.setOrderStatus(OrderStatus.REFUNDED);
        return orderInfo;
    }

    public OrderInfo getExpectedOrderInfo(String userId, Country country, Currency currency,
                                          String locale, boolean isTentative, OrderStatus orderStatus, String paymentId,
                                          String orderId, Map<String, Integer> offers) throws Exception {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(userId);
        orderInfo.setOrderStatus(orderStatus);
        orderInfo.setTentative(isTentative);
        orderInfo.setCountry(country);
        orderInfo.setCurrency(currency);
        orderInfo.setLocale(locale);

        List<String> balanceIds = getBalancesByOrderId(orderId);

        BalanceItem balanceItem = Master.getInstance().getBalance(balanceIds.get(0)).getBalanceItems().get(0);
        BigDecimal taxRate = new BigDecimal(0);

        for (TaxItem taxItem : balanceItem.getTaxItems()) {
            taxRate = taxRate.add(taxItem.getTaxRate());
        }


        orderInfo.setTaxRate(taxRate);

        BigDecimal orderTotalAmount = new BigDecimal(0);
        BigDecimal orderTotalTax = new BigDecimal(0);

        Set<String> key = offers.keySet();
        for (Iterator it = key.iterator(); it.hasNext(); ) {
            OrderItemInfo orderItem = new OrderItemInfo();

            String offerName = (String) it.next();
            int quantity = offers.get(offerName);
            String offerId = offerClient.getOfferIdByName(offerName);
            String currentOfferRevisionId = IdConverter.idToUrlString(OfferRevisionId.class,
                    Master.getInstance().getOffer(offerId).getCurrentRevisionId());
            BigDecimal unitPrice = Master.getInstance().getOfferRevision(currentOfferRevisionId).getPrice()
                    .getPrices().get(country.toString()).get(currency.toString());
            orderItem.setOfferId(offerId);
            orderItem.setUnitPrice(unitPrice);
            orderItem.setQuantity(quantity);
            BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP);
            orderItem.setTotalAmount(totalAmount);
            BigDecimal totalTax = totalAmount.multiply(orderInfo.getTaxRate()).setScale(2, RoundingMode.HALF_UP);
            orderItem.setTotalTax(totalTax);
            orderTotalAmount = orderTotalAmount.add(totalAmount);
            orderTotalTax = orderTotalTax.add(totalTax);
            orderInfo.getOrderItems().add(orderItem);
        }

        PaymentInstrumentInfo paymentInfo = new PaymentInstrumentInfo();
        paymentInfo.setPaymentId(paymentId);
        paymentInfo.setPaymentAmount(orderTotalAmount.add(orderTotalTax));

        orderInfo.getPaymentInfos().add(paymentInfo);

        if (!isTentative) {
            BillingHistoryInfo billingHistory = new BillingHistoryInfo();
            billingHistory.setTotalAmount(orderTotalAmount.add(orderTotalTax));
            paymentInfo.setPaymentAmount(orderTotalAmount.add(orderTotalTax));
            billingHistory.getPaymentInfos().add(paymentInfo);
            billingHistory.setSuccess(true);
            billingHistory.setBillingAction(BillingAction.CHARGE);
            orderInfo.getBillingHistories().add(billingHistory);

        }

        orderInfo.setTotalAmount(orderTotalAmount);
        orderInfo.setTotalTax(orderTotalTax);

        return orderInfo;
    }

    public void refundOrder(String orderId, Map<String, Integer> refundedOffers,
                            Map<String, BigDecimal> partialRefundAmounts) throws Exception {
        refundOrder(orderId, refundedOffers, partialRefundAmounts, 200);
    }

    public void refundOrder(String orderId, Map<String, Integer> refundedOffers,
                            Map<String, BigDecimal> partialRefundAmounts, int expectedResponseCode) throws Exception {
        orderClient.getOrderByOrderId(orderId);
        Order order = Master.getInstance().getOrder(orderId);

        for (int i = 0; i < order.getOrderItems().size(); i++) {
            String offerId = IdConverter.idToHexString(order.getOrderItems().get(i).getOffer());
            OrderItem orderItem = order.getOrderItems().get(i);

            String currentOfferRevisionId = IdConverter.idToUrlString(OfferRevisionId.class,
                    Master.getInstance().getOffer(offerId).getCurrentRevisionId());

            String offerName = Master.getInstance().getOfferRevision(currentOfferRevisionId)
                    .getLocales().get(order.getLocale().getValue()).getName();

            if (refundedOffers != null && refundedOffers.containsKey(offerName)) {
                int refundQuantity = refundedOffers.get(offerName);
                order.getOrderItems().get(i).setQuantity(orderItem.getQuantity() - refundQuantity);
            }

            if (partialRefundAmounts != null && partialRefundAmounts.containsKey(offerName)) {
                BigDecimal refundAmount = partialRefundAmounts.get(offerName);
                order.getOrderItems().get(i).setTotalAmount(orderItem.getTotalAmount().subtract(refundAmount)
                        .setScale(1, RoundingMode.HALF_UP));
            }
        }

        orderClient.refundOrder(order, expectedResponseCode);
    }

    public BigDecimal refundTotalAmount(String orderId) throws Exception {
        orderClient.getOrderByOrderId(orderId);
        BigDecimal totalAmount = new BigDecimal(0);
        Order order = Master.getInstance().getOrder(orderId);
        for (int i = 0; i < order.getOrderItems().size(); i++) {
            totalAmount = totalAmount.add(order.getOrderItems().get(i).getTotalAmount());
            order.getOrderItems().get(i).setTotalAmount(new BigDecimal(0));
        }
        orderClient.updateOrder(order);
        return totalAmount;
    }

    public BigDecimal refundTotalQuantity(String orderId) throws Exception {
        orderClient.getOrderByOrderId(orderId);
        BigDecimal totalAmount = new BigDecimal(0);
        Order order = Master.getInstance().getOrder(orderId);
        for (int i = 0; i < order.getOrderItems().size(); i++) {
            totalAmount = totalAmount.add(order.getOrderItems().get(i).getTotalAmount());
            order.getOrderItems().get(i).setTotalAmount(new BigDecimal(0));
        }
        order.setOrderItems(null);
        orderClient.updateOrder(order);
        return totalAmount;

    }

    public BigDecimal refundPartialAmount(String orderId, BigDecimal refundAmount) throws Exception {
        orderClient.getOrderByOrderId(orderId);
        BigDecimal totalAmount = new BigDecimal(0);
        Order order = Master.getInstance().getOrder(orderId);
        for (int i = 0; i < order.getOrderItems().size(); i++) {
            OrderItem orderItem = order.getOrderItems().get(i);
            BigDecimal orderItemTotalAmount = orderItem.getTotalAmount();
            if (refundAmount.compareTo(orderItemTotalAmount) > 0) {
                throw new TestException("Refund amount more than actual order item amount");
            }
            totalAmount = totalAmount.add(orderItemTotalAmount.subtract(refundAmount));
            order.getOrderItems().get(i).setTotalAmount(orderItemTotalAmount.subtract(refundAmount));
        }

        orderClient.updateOrder(order);
        return totalAmount;
    }

    public BigDecimal refundPartialQuantity(String orderId, int quantity) throws Exception {
        orderClient.getOrderByOrderId(orderId);
        BigDecimal totalAmount = new BigDecimal(0);
        Order order = Master.getInstance().getOrder(orderId);
        for (int i = 0; i < order.getOrderItems().size(); i++) {
            OrderItem orderItem = order.getOrderItems().get(i);
            int itemQuantity = orderItem.getQuantity();
            if (itemQuantity < quantity) {
                throw new TestException("Refund item quantity more than actual quantity!");
            }
            BigDecimal orderItemTotalAmount = orderItem.getTotalAmount();
            BigDecimal unitAmountWithTax = orderItemTotalAmount.divide(new BigDecimal(itemQuantity));
            totalAmount.add(unitAmountWithTax.multiply(new BigDecimal(itemQuantity - quantity)));
            order.getOrderItems().get(i).setQuantity(itemQuantity - quantity);
        }

        orderClient.updateOrder(order);
        return totalAmount;
    }

    public BigDecimal refundPartialItem(String orderId) throws Exception {
        orderClient.getOrderByOrderId(orderId);
        BigDecimal totalAmount = new BigDecimal(0);
        Order order = Master.getInstance().getOrder(orderId);

        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems.size() < 2) {
            throw new TestException("Refund item quantity more than actual quantity!");
        }
        orderItems.remove(0);
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem orderItem = orderItems.get(i);

            BigDecimal orderItemTotalAmount = orderItem.getTotalAmount();
            totalAmount = totalAmount.add(orderItemTotalAmount);
        }

        order.setOrderItems(orderItems);
        orderClient.updateOrder(order);
        return totalAmount;

    }

    public List<String> getBalancesByOrderId(String orderId) throws Exception {
        return billingProvider.getBalancesByOrderId(orderId);
    }

    public String getPaypalToken(String orderId) throws Exception {
        Order order = Master.getInstance().getOrder(orderId);
        order.getPayments().get(0).setSuccessRedirectUrl("http://www.abc.com/");
        order.getPayments().get(0).setCancelRedirectUrl("http://www.abc.com/cancel/");
        orderId = updateOrder(order);

        order = Master.getInstance().getOrder(orderId);
        order.setTentative(false);
        orderId = updateOrder(order);
        order = Master.getInstance().getOrder(orderId);

        String providerConfirmUrl = order.getPayments().get(0).getProviderConfirmUrl();
        int tokenIndex = providerConfirmUrl.indexOf("token=");
        return providerConfirmUrl.substring(tokenIndex + 6);
    }

    public String getProviderConfirmUrl(String orderId) throws Exception {
        Order order = Master.getInstance().getOrder(orderId);
        order.getPayments().get(0).setSuccessRedirectUrl("http://www.abc.com/");
        order.getPayments().get(0).setCancelRedirectUrl("http://www.abc.com/cancel/");
        orderId = updateOrder(order);

        order = Master.getInstance().getOrder(orderId);
        order.setTentative(false);
        orderId = updateOrder(order);
        order = Master.getInstance().getOrder(orderId);

        return order.getPayments().get(0).getProviderConfirmUrl();
    }

    public void emulatePaypalCallback(String orderId, String token) throws Exception {
        String paymentTransactionId = "";
        Map<String, String> properties = new HashMap<>();
        properties.put("paymentId", paymentTransactionId);
        properties.put("payerId", payerId);
        properties.put("token", token);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(properties);

        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrder(new OrderId(IdConverter.hexStringToId(OrderId.class, orderId)));
        orderEvent.setAction("CHARGE");
        orderEvent.setStatus("COMPLETED");
        orderEvent.setProperties(json);
        orderEventClient.postOrderEvent(orderEvent);
    }

    public void emulateAdyenCallBack(String orderId) throws Exception {
        String successRedirectUrl = "";
        String paymentTransactionId = "";
        String authResult = "AUTHORISED";
        String pspReference = "";
        String skinCode = "";
        String merchantSig = "";
        String[] params = successRedirectUrl.split("&");
        for (String param : params) {
            String value = param.substring(param.indexOf('=') + 1);
            if (param.contains("merchantReference")) {
                paymentTransactionId = value;
            } else if (param.contains("skinCode")) {
                skinCode = value;
            } else if (param.contains("psp")) {
                pspReference = value;
            } else if (param.contains("merchantSig")) {
                //merchantSig = URLEncoder.encode(value, "utf-8");
                merchantSig = URLDecoder.decode(value, "utf-8");
            }
        }

        Map<String, String> properties = new HashMap<>();
        properties.put("paymentId", paymentTransactionId);
        properties.put("pspReference", pspReference);
        properties.put("authResult", authResult);
        properties.put("skinCode", skinCode);
        properties.put("merchantSig", merchantSig);
        properties.put("merchantReference", paymentTransactionId);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(properties);


        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrder(new OrderId(IdConverter.hexStringToId(OrderId.class, orderId)));
        orderEvent.setAction("CHARGE");
        orderEvent.setStatus("COMPLETED");
        orderEvent.setProperties(json);

        orderEventClient.postOrderEvent(orderEvent);
    }

}



