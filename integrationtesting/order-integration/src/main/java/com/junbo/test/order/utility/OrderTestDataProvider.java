/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.test.order.utility;

import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.*;
import com.junbo.common.model.Results;
import com.junbo.order.spec.model.*;
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
import com.junbo.test.order.model.enums.EventStatus;
import com.junbo.test.order.model.enums.OrderActionType;
import com.junbo.test.payment.utility.PaymentTestDataProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        OfferRevision offerRevisionUpdated = offerRevisionClient.postOfferRevision(offerRevision);
        offerRevisionUpdated.setStatus("APPROVED");
        offerRevisionClient.updateOfferRevision(offerRevisionUpdated.getRevisionId(), offerRevisionUpdated);
    }

    public String postOrder(String uid, Country country, Currency currency, String paymentInstrumentId,
                            boolean hasPhysicalGood, ArrayList<String> offers)
            throws Exception {
        return postOrder(uid, country, currency, paymentInstrumentId, hasPhysicalGood, 1, offers, 200);
    }

    public String postOrder(String uid, Country country, Currency currency, String paymentInstrumentId,
                            boolean hasPhysicalGood, int quantity, ArrayList<String> offers)
            throws Exception {
        return postOrder(uid, country, currency, paymentInstrumentId, hasPhysicalGood, quantity, offers, 200);
    }

    public String postOrder(String uid, Country country, Currency currency, String paymentInstrumentId,
                            boolean hasPhysicalGood, int quantity, ArrayList<String> offers, int expectedResponseCode)
            throws Exception {
        Order order = new Order();
        order.setUser(new UserId(IdConverter.hexStringToId(UserId.class, uid)));
        order.setCountry(new CountryId(country.toString()));
        order.setCurrency(new CurrencyId(currency.toString()));
        List<PaymentInfo> paymentInfos = new ArrayList<>();
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentInstrument(new PaymentInstrumentId(
                IdConverter.hexStringToId(PaymentInstrumentId.class, paymentInstrumentId)));
        paymentInfos.add(paymentInfo);
        order.setPayments(paymentInfos);
        order.setShippingMethod("0L");

        if (hasPhysicalGood) {
            order.setShippingMethod(String.valueOf(quantity));
            order.setShippingAddress(Master.getInstance().getUser(uid).getAddresses().get(0).getValue());
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        for (int i = 0; i < offers.size(); i++) {
            OfferId offerId = new OfferId(offerClient.getOfferIdByName(offers.get(i)));

            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(1);
            //orderItem.setQuantity(Integer.valueOf(RandomFactory.getRandomLong(1L, 1L).toString()));
            orderItem.setOffer(offerId);
            orderItemList.add(orderItem);
        }
        order.setOrderItems(orderItemList);
        order.setTentative(true);
        order.setLocale(new LocaleId("en_US"));
        return orderClient.postOrder(order, expectedResponseCode);
    }


    public String postOrder(String uid, Country country, Currency currency, String paymentInstrumentId,
                            boolean hasPhysicalGood, ArrayList<String> offers, int expectedResponseCode)
            throws Exception {

        return postOrder(uid, country, currency, paymentInstrumentId, hasPhysicalGood, 1, offers, expectedResponseCode);
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

    public String postOrder(Order order) throws Exception {
        return orderClient.postOrder(order);
    }

    public Subledger getSubledger(String offerName) throws Exception {
        String offerId = offerClient.getOfferIdByName(offerName);
        Offer offer = Master.getInstance().getOffer(offerId);
        String offerRevisionId = IdConverter.idToUrlString(OfferRevisionId.class, offer.getCurrentRevisionId());
        String sellerId = IdConverter.idToUrlString(OrganizationId.class,
                Master.getInstance().getOfferRevision(offerRevisionId).getOwnerId().getValue());
        return orderClient.getSubledger(sellerId);
    }

    public void invalidateCreditCard(String uid, String paymentId) throws Exception {
        paymentProvider.invalidateCreditCard(uid, paymentId);
    }

    public Results<OrderEvent> getOrderEventsByOrderId(String orderId) throws Exception {
        return orderEventClient.getOrderEventsByOrderId(orderId);
    }

    public void postOrderEvent(String orderId, EventStatus eventStatus, OrderActionType orderActionType)
            throws Exception {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrder(new OrderId(IdConverter.hexStringToId(OrderId.class, orderId)));
        orderEvent.setAction(orderActionType.toString());
        orderEvent.setStatus(eventStatus.toString());
        orderEventClient.postOrderEvent(orderEvent);
    }

    public BigDecimal refundTotalAmount(String orderId) throws Exception {
        orderClient.getOrderByOrderId(orderId);
        BigDecimal totalAmount = new BigDecimal(0);
        Order order = Master.getInstance().getOrder(orderId);
        for (int i = 0; i < order.getOrderItems().size(); i++) {
            totalAmount.add(order.getOrderItems().get(i).getTotalAmount());
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
            totalAmount.add(order.getOrderItems().get(i).getTotalAmount());
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
            totalAmount.add(orderItemTotalAmount.subtract(refundAmount));
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

    public String getBalanceByOrderId(String uid, String orderId) throws Exception {
        return billingProvider.getBalanceByOrderId(uid, orderId);
    }

}
