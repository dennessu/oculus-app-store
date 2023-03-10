/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing.utility;

import com.junbo.billing.spec.model.Balance;
import com.junbo.billing.spec.model.BalanceItem;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.*;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderItem;
import com.junbo.order.spec.model.PaymentInfo;
import com.junbo.test.billing.apihelper.BalanceService;
import com.junbo.test.billing.apihelper.impl.BalanceServiceImpl;
import com.junbo.test.billing.enums.BalanceType;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.PaymentInstrumentBase;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.order.OrderService;
import com.junbo.test.common.apihelper.order.impl.OrderServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.DBHelper;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.ShardIdHelper;
import com.junbo.test.payment.utility.PaymentTestDataProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Yunlong on 4/9/14.
 */
public class BillingTestDataProvider extends BaseTestDataProvider {
    private UserService identityClient = UserServiceImpl.instance();
    private PaymentTestDataProvider paymentProvider = new PaymentTestDataProvider();
    private OfferService offerClient = OfferServiceImpl.instance();
    private OrderService orderClient = OrderServiceImpl.getInstance();
    private BalanceService balanceClient = BalanceServiceImpl.getInstance();
    //private ShippingAddressService shippingClient = ShippingAddressServiceImpl.getInstance();
    private DBHelper dbHelper = new DBHelper();


    public BillingTestDataProvider() {
        super();
    }

    public String CreateUser() throws Exception {
        return identityClient.PostUser();
    }

    public String postPaymentInstrument(String uid, PaymentInstrumentBase paymentInfo) throws Exception {
        return paymentProvider.postPaymentInstrument(uid, paymentInfo);
    }


    public String postOrder(String uid, Country country, Currency currency, String paymentInstrumentId,
                            String shippingAddressId, ArrayList<String> offers) throws Exception {
        Order order = new Order();

        List<PaymentInstrumentId> paymentInstruments = new ArrayList<>();
        paymentInstruments.add(new PaymentInstrumentId(
                IdConverter.hexStringToId(PaymentInstrumentId.class, paymentInstrumentId)));
        order.setUser(new UserId(IdConverter.hexStringToId(UserId.class, uid)));
        order.setCountry(new CountryId(country.toString()));
        order.setCurrency(new CurrencyId(currency.toString()));
        //order.setPaymentInstruments(paymentInstruments);
        List<PaymentInfo> paymentInfos = new ArrayList<>();
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentInstrument(new PaymentInstrumentId(
                IdConverter.hexStringToId(PaymentInstrumentId.class, paymentInstrumentId)));
        paymentInfos.add(paymentInfo);
        order.setPayments(paymentInfos);
        order.setShippingMethod("0L");

        if (shippingAddressId != null) {
            //order.setShippingAddress(Master.getInstance().getShippingAddress(shippingAddressId).getAddressId());
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
        return orderClient.postOrder(order);
    }

    public String quoteBalance(String uid, String pid) throws Exception {
        Balance balance = new Balance();


        balance.setUserId(new UserId(IdConverter.hexStringToId(UserId.class, uid)));
        balance.setTrackingUuid(UUID.randomUUID());
        balance.setCountry(Country.DEFAULT.toString());
        balance.setCurrency(Currency.DEFAULT.toString());
        balance.setPiId(new PaymentInstrumentId(IdConverter.hexStringToId(PaymentInstrumentId.class, pid)));
        balance.setType(BalanceType.DEBIT.toString());
        balance.setIsAsyncCharge(false);


        BalanceItem balanceItem = new BalanceItem();
        balanceItem.setAmount(new BigDecimal(10));
        balanceItem.setOrderItemId(new OrderItemId(0L));
        balance.addBalanceItem(balanceItem);

        return balanceClient.quoteBalance(uid, balance);
    }

    public String postBalanceByOrderId(String uid, String orderId) throws Exception {

        Balance balance = new Balance();
        Order order = Master.getInstance().getOrder(orderId);

        balance.setUserId(new UserId(IdConverter.hexStringToId(UserId.class, uid)));
        balance.setTrackingUuid(UUID.randomUUID());
        balance.setCountry(order.getCountry().getValue());
        List<OrderId> orderIds = new ArrayList<>();
        orderIds.add(order.getId());
        balance.setOrderIds(orderIds);
        balance.setCurrency(order.getCurrency().getValue());
        //balance.setDiscountAmount(order.getTotalDiscount());
        balance.setShippingAddressId(order.getShippingAddress());
        balance.setPiId(order.getPayments().get(0).getPaymentInstrument());
        balance.setType(BalanceType.DEBIT.toString());
        //balance.setStatus(BalanceStatus.AWAITING_PAYMENT.toString());
        //balance.setTotalAmount(order.getTotalAmount());
        //balance.setTaxAmount(order.getTotalTax());
        //balance.setTaxStatus(BalanceTaxStatus.TAXED.toString());
        balance.setIsAsyncCharge(false);

        for (int i = 0; i < order.getOrderItems().size(); i++) {
            OrderItem orderItem = order.getOrderItems().get(i);
            BalanceItem balanceItem = new BalanceItem();
            balanceItem.setAmount(orderItem.getTotalAmount());
            String orderItemId = getOrderItemId(uid, IdConverter.hexStringToId(OrderId.class, orderId),
                    orderItem.getOffer().getValue());
            balanceItem.setOrderItemId(new OrderItemId(Long.valueOf(orderItemId)));
            //balanceItem.setDiscountAmount(orderItem.getTotalDiscount());
            //balanceItem.setTaxAmount(orderItem.getTotalTax());
            balance.addBalanceItem(balanceItem);
        }

        return balanceClient.postBalance(uid, balance);

    }

    public String getOrderItemId(String uid, Long orderId, String offerId) throws Exception {
        String sqlStr = String.format(
                "select order_item_id from shard_%s.order_item where order_id='%s' and offer_id='%s'",
                ShardIdHelper.getShardIdByUid(uid), orderId, offerId);
        return dbHelper.executeScalar(sqlStr, DBHelper.DBName.ORDER);
    }

    public String updateOrderTentative(String orderId, boolean isTentative) throws Exception {
        Order order = Master.getInstance().getOrder(orderClient.getOrderByOrderId(orderId));
        order.setTentative(isTentative);
        return orderClient.updateOrder(order);
    }

    public List<String> getBalancesByOrderId(String orderId) throws Exception {
        return balanceClient.getBalancesByOrderId(orderId);
    }

    public String getBalanceByBalanceId(String uid, String balanceId) throws Exception {
        return balanceClient.getBalanceByBalanceId(uid, balanceId);
    }

}
