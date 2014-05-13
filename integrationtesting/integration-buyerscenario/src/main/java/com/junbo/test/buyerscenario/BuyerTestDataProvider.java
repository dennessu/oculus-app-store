/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.buyerscenario;

import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.order.spec.model.OrderItem;
import com.junbo.order.spec.model.PaymentInfo;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.TypeSpecificDetails;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.common.Entities.paymentInstruments.EwalletInfo;
import com.junbo.test.common.Entities.paymentInstruments.PayPalInfo;
import com.junbo.test.common.Entities.paymentInstruments.PaymentInstrumentBase;
import com.junbo.test.common.Utility.BaseTestDataProvider;

import com.junbo.test.common.apihelper.cart.CartService;
import com.junbo.test.common.apihelper.cart.impl.CartServiceImpl;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.order.spec.model.Order;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.order.OrderService;
import com.junbo.test.common.apihelper.order.impl.OrderServiceImpl;

import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.EnumHelper.UserStatus;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.payment.apihelper.PaymentService;
import com.junbo.test.payment.apihelper.impl.PaymentServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yunlong on 3/20/14.
 */
public class BuyerTestDataProvider extends BaseTestDataProvider {
    private UserService identityClient = UserServiceImpl.instance();
    private ItemService itemClient = ItemServiceImpl.instance();
    private OfferService offerClient = OfferServiceImpl.instance();
    private CartService cartClient = CartServiceImpl.getInstance();
    private OrderService orderClient = OrderServiceImpl.getInstance();
    private PaymentService paymentClient = PaymentServiceImpl.getInstance();

    private LogHelper logger = new LogHelper(BuyerTestDataProvider.class);


    public BuyerTestDataProvider() {
    }

    public String createUser(String email, String password, UserStatus status) throws Exception {
        User userToPost = new User();
        //userToPost.setUserName(email);
        //userToPost.setPassword(password);
        logger.LogSample("Create a new user");
        return identityClient.PostUser(userToPost);
    }

    public String createUser() throws Exception {
        return identityClient.PostUser();
    }

    public String getUserByUid(String userId) throws Exception {
        return identityClient.GetUserByUserId(userId);
    }

    public String postDefaultItem() throws Exception {
        Item item = itemClient.postDefaultItem(CatalogItemType.PHYSICAL);
        return IdConverter.idLongToHexString(ItemId.class, item.getItemId());
    }

    public String postDefaultOffer(CatalogItemType itemType) throws Exception {
        logger.LogSample("Post a offer");
        Offer offer = offerClient.postDefaultOffer();
        return IdConverter.idLongToHexString(OfferId.class, offer.getOfferId());
    }

    public String postOffersToPrimaryCart(String uid, boolean hasPhysicalGood, ArrayList<String> offers) throws Exception {
        String primaryCartId = cartClient.getCartPrimary(uid);
        Cart primaryCart = Master.getInstance().getCart(primaryCartId);
        List<OfferItem> offerItemList = new ArrayList<>();
        for (int i = 0; i < offers.size(); i++) {
            OfferItem offerItem = new OfferItem();
            if (hasPhysicalGood) {
                offerItem.setQuantity(RandomFactory.getRandomLong(1L, 5L));
            } else {
                offerItem.setQuantity(1L);
            }
            offerItem.setIsSelected(true);
            OfferId offerId = new OfferId(
                    IdConverter.hexStringToId(OfferId.class, offerClient.getOfferIdByName(offers.get(i))));
            offerItem.setOffer(offerId);
            offerItem.setIsApproved(true);
            offerItemList.add(offerItem);
        }
        primaryCart.setOffers(offerItemList);

        Master.getInstance().addCart(primaryCartId, primaryCart);
        return cartClient.updateCart(uid, primaryCartId, primaryCart);
    }

    public String postDefaultOffersToPrimaryCart(String uid, CatalogItemType itemType) throws Exception {
        String offerId = this.postDefaultOffer(itemType);
        //String offerId = IdConverter.idLongToHexString(OfferId.class, new OfferId(100001L).getValue());
        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offerId);
        return this.postOffersToPrimaryCart(uid, false, offerList);
    }

    public String postPaymentInstrument(String uid, PaymentInstrumentBase paymentInfo) throws Exception {

        PaymentInstrument paymentInstrument = new PaymentInstrument();
        ArrayList<Long> admins = new ArrayList<>();
        admins.add(IdConverter.hexStringToId(UserId.class, uid));
        paymentInstrument.setAdmins(admins);
        paymentInstrument.setLabel("4");
        TypeSpecificDetails typeSpecificDetails = new TypeSpecificDetails();
        Long billingAddressId = Master.getInstance().getUser(uid).getAddresses().get(0).getValue().getValue();
        paymentInfo.setBillingAddressId(billingAddressId);
        switch (paymentInfo.getType()) {
            case CREDITCARD:
                CreditCardInfo creditCardInfo = (CreditCardInfo) paymentInfo;
                typeSpecificDetails.setExpireDate(creditCardInfo.getExpireDate());
                typeSpecificDetails.setEncryptedCvmCode(creditCardInfo.getEncryptedCVMCode());
                paymentInstrument.setTypeSpecificDetails(typeSpecificDetails);
                paymentInstrument.setAccountName(creditCardInfo.getAccountName());
                paymentInstrument.setAccountNum(creditCardInfo.getAccountNum());
                paymentInstrument.setIsValidated(creditCardInfo.isValidated());
                paymentInstrument.setType(creditCardInfo.getType().getValue());
                paymentInstrument.setBillingAddressId(creditCardInfo.getBillingAddressId());

                paymentInfo.setPid(paymentClient.postPaymentInstrument(paymentInstrument));
                return paymentInfo.getPid();

            case EWALLET:
                EwalletInfo ewalletInfo = (EwalletInfo) paymentInfo;
                typeSpecificDetails.setStoredValueCurrency("usd");
                paymentInstrument.setTypeSpecificDetails(typeSpecificDetails);
                paymentInstrument.setAccountName(ewalletInfo.getAccountName());
                paymentInstrument.setType(ewalletInfo.getType().getValue());
                paymentInstrument.setIsValidated(ewalletInfo.isValidated());
                paymentInstrument.setBillingAddressId(billingAddressId);
                paymentInstrument.setBillingAddressId(ewalletInfo.getBillingAddressId());

                paymentInfo.setPid(paymentClient.postPaymentInstrument(paymentInstrument));
                return paymentInfo.getPid();

            case PAYPAL:
                PayPalInfo payPalInfo = (PayPalInfo) paymentInfo;
                paymentInstrument.setAccountName(payPalInfo.getAccountName());
                paymentInstrument.setAccountNum(payPalInfo.getAccountNum());
                paymentInstrument.setIsValidated(payPalInfo.isValidated());
                paymentInstrument.setType(payPalInfo.getType().getValue());
                paymentInstrument.setBillingAddressId(payPalInfo.getBillingAddressId());

                paymentInfo.setPid(paymentClient.postPaymentInstrument(paymentInstrument));
                return paymentInfo.getPid();

            default:
                throw new TestException(String.format("%s is not supported", paymentInfo.getType().toString()));
        }
    }

    public void creditWallet(String uid) throws Exception {
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setCurrency("usd");
        creditRequest.setUserId(IdConverter.hexStringToId(UserId.class, uid));
        creditRequest.setAmount(new BigDecimal(500));
        paymentClient.creditWallet(creditRequest);
    }

    public void creditWallet(String uid, BigDecimal amount) throws Exception {
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setCurrency("usd");
        creditRequest.setUserId(IdConverter.hexStringToId(UserId.class, uid));
        creditRequest.setAmount(amount);
        paymentClient.creditWallet(creditRequest);
    }

/*    public String postShippingAddressToUser(String uid, ShippingAddressInfo shippingAddressInfo) throws Exception {
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setStreet(shippingAddressInfo.getStreet());
        shippingAddress.setCity(shippingAddressInfo.getCity());
        shippingAddress.setState(shippingAddressInfo.getState());
        shippingAddress.setPostalCode(shippingAddressInfo.getPostalCode());
        shippingAddress.setCountry(shippingAddressInfo.getCountry());
        shippingAddress.setFirstName(shippingAddressInfo.getFirstName());
        shippingAddress.setLastName(shippingAddressInfo.getLastName());
        shippingAddress.setPhoneNumber(shippingAddressInfo.getPhoneNumber());
        logger.LogSample("Post shipping address to user");
        return shippingClient.postShippingAddressToUser(uid, shippingAddress);
    }*/

    public String postOrderByCartId(String uid, String cartId, Country country, Currency currency,
                                    String paymentInstrumentId) throws Exception {
        return postOrderByCartId(uid, cartId, country, currency, paymentInstrumentId, null);
    }

    public String postOrder(String uid, Country country, Currency currency, String paymentInstrumentId,
                            boolean hasPhysicalGood, ArrayList<String> offers) throws Exception {

        return this.postOrder(uid, country, currency, paymentInstrumentId, hasPhysicalGood, offers, 200);
    }

    public String postOrder(String uid, Country country, Currency currency, String paymentInstrumentId,
                            boolean hasPhysicalGood, ArrayList<String> offers, int expectedResponseCode)
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
        order.setShippingMethod(0L);

        if (hasPhysicalGood) {
            order.setShippingAddress(Master.getInstance().getUser(uid).getAddresses().get(0).getValue());
        }
        List<OrderItem> orderItemList = new ArrayList<>();
        for (int i = 0; i < offers.size(); i++) {
            OfferId offerId = new OfferId(
                    IdConverter.hexStringToId(OfferId.class, offerClient.getOfferIdByName(offers.get(i))));

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

    public String postOrderByCartId(String uid, String cartId, Country country, Currency currency,
                                    String paymentInstrumentId, String shippingAddressId) throws Exception {
        return this.postOrderByCartId(uid, cartId, country, currency, paymentInstrumentId, false);
    }

    public String postOrderByCartId(String uid, String cartId, Country country, Currency currency,
                                    String paymentInstrumentId, boolean hasPhysicalGood)
            throws Exception {
        if (cartId == null) {
            cartId = cartClient.getCartPrimary(uid);
        }

        Cart cart = Master.getInstance().getCart(cartId);
        Order order = new Order();

        List<PaymentInfo> paymentInfos = new ArrayList<>();
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentInstrument(new PaymentInstrumentId(
                IdConverter.hexStringToId(PaymentInstrumentId.class, paymentInstrumentId)));
        paymentInfos.add(paymentInfo);
        order.setPayments(paymentInfos);
        order.setUser(cart.getUser());
        order.setCountry(new CountryId(country.toString()));
        order.setCurrency(new CurrencyId(currency.toString()));

        List<OrderItem> orderItemList = new ArrayList<>();
        List<OfferItem> offerItemList = cart.getOffers();
        for (int i = 0; i < offerItemList.size(); i++) {
            OfferId offerId = offerItemList.get(i).getOffer();

            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(Integer.parseInt(offerItemList.get(i).getQuantity().toString()));
            orderItem.setOffer(offerId);
            orderItemList.add(orderItem);
        }
        if (hasPhysicalGood) {
            order.setShippingMethod(01L);
            order.setShippingAddress(Master.getInstance().getUser(uid).getAddresses().get(0).getValue());
        }
        order.setOrderItems(orderItemList);
        order.setTentative(true);

        order.setLocale(new LocaleId("en_US"));
        logger.LogSample("Post an order");
        return orderClient.postOrder(order);
    }


    public String updateOrderTentative(String orderId, boolean isTentative) throws Exception {
        return this.updateOrderTentative(orderId, isTentative, 200);
    }

    public String updateOrderTentative(String orderId, boolean isTentative, int expectedResponseCode) throws Exception {
        Order order = Master.getInstance().getOrder(orderClient.getOrderByOrderId(orderId));
        order.setTentative(isTentative);
        logger.LogSample("Put an order");
        return orderClient.updateOrder(order, expectedResponseCode);
    }

    public void emptyCartByCartId(String uid, String cartId) throws Exception {
        if (cartId == null) {
            cartId = cartClient.getCartPrimary(uid);
        }
        logger.LogSample("Put cart");
        cartClient.updateCart(uid, cartId, new Cart());
    }

    public String updateOrder(Order order) throws Exception {
        return orderClient.updateOrder(order);
    }

    public String getOrder(String orderId) throws Exception {
        return orderClient.getOrderByOrderId(orderId);
    }

}
