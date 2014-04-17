/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing.utility;

import com.junbo.billing.spec.model.Balance;
import com.junbo.billing.spec.model.BalanceItem;
import com.junbo.common.id.*;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderItem;
import com.junbo.payment.spec.model.Address;
import com.junbo.payment.spec.model.CreditCardRequest;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.test.billing.apihelper.BalanceService;
import com.junbo.test.billing.apihelper.ShippingAddressService;
import com.junbo.test.billing.apihelper.impl.BalanceServiceImpl;
import com.junbo.test.billing.apihelper.impl.ShippingAddressServiceImpl;
import com.junbo.test.billing.enums.BalanceType;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Entities.paymentInstruments.PaymentInstrumentBase;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.common.apihelper.catalog.OfferService;
import com.junbo.test.common.apihelper.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.order.OrderService;
import com.junbo.test.common.apihelper.order.impl.OrderServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.DBHelper;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.payment.apihelper.PaymentService;
import com.junbo.test.payment.apihelper.impl.PaymentServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Yunlong on 4/9/14.
 */
public class BillingTestDataProvider extends BaseTestDataProvider {
    private UserService identityClient = UserServiceImpl.instance();
    private PaymentService paymentClient = PaymentServiceImpl.getInstance();
    private OfferService offerClient = OfferServiceImpl.instance();
    private OrderService orderClient = OrderServiceImpl.getInstance();
    private BalanceService balanceClient = BalanceServiceImpl.getInstance();
    private ShippingAddressService shippingAddressClient = ShippingAddressServiceImpl.getInstance();
    private DBHelper dbHelper = new DBHelper();


    public BillingTestDataProvider() {
        super();
    }

    public String CreateUser() throws Exception {
        return identityClient.PostUser();
    }

    public String postPaymentInstrument(String uid, PaymentInstrumentBase paymentInfo) throws Exception {
        switch (paymentInfo.getType()) {
            case CREDITCARD:
                PaymentInstrument paymentInstrument = new PaymentInstrument();
                CreditCardInfo creditCardInfo = (CreditCardInfo) paymentInfo;
                CreditCardRequest creditCardRequest = new CreditCardRequest();
                //creditCardRequest.setType(creditCardInfo.getType().toString());
                creditCardRequest.setExpireDate(creditCardInfo.getExpireDate());
                creditCardRequest.setEncryptedCvmCode(creditCardInfo.getEncryptedCVMCode());

                Address address = new Address();
                address.setAddressLine1(creditCardInfo.getAddress().getAddressLine1());
                address.setCity(creditCardInfo.getAddress().getCity());
                address.setState(creditCardInfo.getAddress().getState());
                address.setCountry(creditCardInfo.getAddress().getCountry());
                address.setPostalCode(creditCardInfo.getAddress().getPostalCode());
                ArrayList<Long> admins = new ArrayList<>();
                admins.add(IdConverter.hexStringToId(UserId.class,uid));
                paymentInstrument.setAdmins(admins);
                paymentInstrument.setAccountName(creditCardInfo.getAccountName());
                paymentInstrument.setAccountNum(creditCardInfo.getAccountNum());
                //paymentInstrument.setAccountNum(creditCardInfo.getAccountNum());
                paymentInstrument.setAddress(address);
                paymentInstrument.setCreditCardRequest(creditCardRequest);
                paymentInstrument.setPhoneNum("650-253-0000");
                paymentInstrument.setIsValidated(creditCardInfo.isValidated());
                paymentInstrument.setType(creditCardInfo.getType().toString());
                paymentInstrument.setTrackingUuid(UUID.randomUUID());

                return paymentClient.postPaymentInstrumentToUser(uid, paymentInstrument);
            default:
                throw new TestException(String.format("%s is not supported", paymentInfo.getType().toString()));
        }

    }

    public String postOrder(String uid, Country country, Currency currency, String paymentInstrumentId,
                            String shippingAddressId, ArrayList<String> offers) throws Exception {
        Order order = new Order();

        List<PaymentInstrumentId> paymentInstruments = new ArrayList<>();
        paymentInstruments.add(new PaymentInstrumentId(
                IdConverter.hexStringToId(PaymentInstrumentId.class, paymentInstrumentId)));
        order.setUser(new UserId(IdConverter.hexStringToId(UserId.class, uid)));
        order.setCountry(country.toString());
        order.setCurrency(currency.toString());
        order.setPaymentInstruments(paymentInstruments);

        if (shippingAddressId != null) {
            order.setShippingAddress(Master.getInstance().getShippingAddress(shippingAddressId).getAddressId());
        }
        List<OrderItem> orderItemList = new ArrayList<>();
        for (int i = 0; i < offers.size(); i++) {
            OfferId offerId = new OfferId(
                    IdConverter.hexStringToId(OfferId.class, offerClient.getOfferIdByName(offers.get(i))));

            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(Integer.valueOf(RandomFactory.getRandomLong(1L, 5L).toString()));
            orderItem.setOffer(offerId);
            orderItemList.add(orderItem);
        }
        order.setOrderItems(orderItemList);
        order.setTentative(true);
        order.setType("PAY_IN");
        order.setLocale("en_US");
        return orderClient.postOrder(order);
    }

    public String postBalanceByOrderId(String uid, String orderId) throws Exception {
        Balance balance = new Balance();
        Order order = Master.getInstance().getOrder(orderId);

        balance.setUserId(new UserId(IdConverter.hexStringToId(UserId.class, uid)));
        balance.setTrackingUuid(UUID.randomUUID());
        balance.setCountry(order.getCountry());
        balance.setOrderId(order.getId());
        balance.setCurrency(order.getCurrency());
        //balance.setDiscountAmount(order.getTotalDiscount());
        balance.setShippingAddressId(order.getShippingAddress());
        balance.setPiId(order.getPaymentInstruments().get(0));
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
            String orderItemId = getOrderItemId(IdConverter.hexStringToId(OrderId.class, orderId),
                    orderItem.getOffer().getValue());
            balanceItem.setOrderItemId(new OrderItemId(Long.valueOf(orderItemId)));
            //balanceItem.setDiscountAmount(orderItem.getTotalDiscount());
            //balanceItem.setTaxAmount(orderItem.getTotalTax());
            balance.addBalanceItem(balanceItem);
        }

        return balanceClient.postBalance(uid, balance);
    }

    private String getOrderItemId(Long orderId, Long offerId) throws Exception {
        String sqlStr = String.format(
                "select order_item_id from order_item where order_id='%s' and product_item_id='%s'", orderId, offerId);
        return dbHelper.executeScalar(sqlStr, DBHelper.DBName.ORDER);
    }

    public String updateOrderTentative(String orderId, boolean isTentative) throws Exception {
        Order order = Master.getInstance().getOrder(orderClient.getOrderByOrderId(orderId));
        order.setTentative(isTentative);
        return orderClient.updateOrder(order);
    }

    public String getBalanceByOrderId(String uid, String orderId) throws Exception {
        return balanceClient.getBalanceByOrderId(uid, orderId);
    }

    public String getBalanceByBalanceId(String uid, String balanceId) throws Exception{
        return balanceClient.getBalanceByBalanceId(uid, balanceId);
    }

}
