/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.Utility;

import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.spec.model.item.CouponItem;
import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.common.id.OfferId;
import com.junbo.identity.spec.model.user.User;
import com.junbo.order.spec.model.OrderItem;
import com.junbo.payment.spec.model.Address;
import com.junbo.payment.spec.model.CreditCardRequest;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.Phone;
import com.junbo.testing.common.apihelper.billing.ShippingAddressService;
import com.junbo.testing.common.apihelper.billing.impl.ShippingAddressServiceImpl;
import com.junbo.testing.common.apihelper.cart.CartService;
import com.junbo.testing.common.apihelper.cart.impl.CartServiceImpl;
import com.junbo.testing.common.apihelper.catalog.ItemService;
import com.junbo.testing.common.apihelper.catalog.OfferService;
import com.junbo.testing.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.testing.common.apihelper.catalog.impl.OfferServiceImpl;
import com.junbo.order.spec.model.Order;
import com.junbo.testing.common.apihelper.identity.UserService;
import com.junbo.testing.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.testing.common.apihelper.order.OrderService;
import com.junbo.testing.common.apihelper.order.impl.OrderServiceImpl;

import com.junbo.testing.common.apihelper.payment.PaymentService;
import com.junbo.testing.common.apihelper.payment.impl.PaymentServiceImpl;
import com.junbo.testing.common.blueprint.Master;
import com.junbo.testing.common.enums.Country;
import com.junbo.testing.common.enums.Currency;
import com.junbo.testing.common.libs.EnumHelper.UserStatus;
import com.junbo.testing.common.libs.RandomFactory;
import com.junbo.testing.common.paymentInstruments.CreditCardInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Yunlong on 3/20/14.
 */
public class TestDataProvider {
    private UserService identityClient = UserServiceImpl.instance();
    private ItemService itemClient = ItemServiceImpl.instance();
    private OfferService offerClient = OfferServiceImpl.instance();
    private CartService cartClient = CartServiceImpl.getInstance();
    private OrderService orderClient = OrderServiceImpl.getInstance();
    private PaymentService paymentClient = PaymentServiceImpl.getInstance();
    private ShippingAddressService shippingClient = ShippingAddressServiceImpl.getInstance();


    public TestDataProvider() {
    }

    public String createUser(String email, String password, UserStatus status) throws Exception {
        User userToPost = new User();
        userToPost.setUserName(email);
        userToPost.setPassword(password);
        userToPost.setStatus(status.getStatus());

        return identityClient.PostUser(userToPost);
    }

    public String createUser() throws Exception {
        return identityClient.PostUser();
    }

    public String getUserByUid(String userId) throws Exception {
        return identityClient.GetUserByUserId(userId);
    }

    public String postDefaultItem() throws Exception {
        return itemClient.postDefaultItem(true);
    }

    public String postDefaultOffer(boolean isPhysical) throws Exception {
        return offerClient.postDefaultOffer(isPhysical);
    }

    public void postOffersToPrimaryCart(String uid, ArrayList<String> offers) throws Exception {
        String primaryCartId = cartClient.getCartPrimary(uid);
        Cart primaryCart = Master.getInstance().getCart(primaryCartId);
        List<OfferItem> offerItemList = new ArrayList<>();
        List<CouponItem> couponItemList = new ArrayList<>();
        for (int i = 0; i < offers.size(); i++) {
            OfferItem offerItem = new OfferItem();
            offerItem.setQuantity(RandomFactory.getRandomLong(5L));
            offerItem.setSelected(true);
            OfferId offerId = new OfferId();
            offerId.setValue(Master.getInstance().getOffer(offers.get(i)).getId());
            offerItem.setOffer(offerId);

            offerItemList.add(offerItem);
        }
        primaryCart.setOffers(offerItemList);
        primaryCart.setCoupons(couponItemList);

        Master.getInstance().addCart(primaryCartId, primaryCart);
        cartClient.updateCart(uid, primaryCartId, primaryCart);
    }

    public void postDefaultOffersToPrimaryCart(String uid, boolean isPhysical) throws Exception {
        String offerId = this.postDefaultOffer(isPhysical);
        //String offerId = IdConverter.idLongToHexString(OfferId.class, new OfferId(100001L).getValue());
        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offerId);
        this.postOffersToPrimaryCart(uid, offerList);
    }

    public void mergeCart(String destinationUid, String sourceUid) throws Exception {
        String sourceCartId = cartClient.getCartPrimary(sourceUid);
        Cart sourceCart = Master.getInstance().getCart(sourceCartId);

        String destinationCartId = cartClient.getCartPrimary(destinationUid);
        //Cart destinationCart = Master.getInstance().getCart(destinationCartId);

        cartClient.mergeCart(destinationUid, destinationCartId, sourceCart);
    }

    public String transferPrimaryCartToOrder(String uid) throws Exception {
        return transferPrimaryCartToOrder(uid, Country.DEFAULT, Currency.DEFAULT);
    }


    public String transferPrimaryCartToOrder(String uid, Country country, Currency currency) throws Exception {
        String primaryCartId = cartClient.getCartPrimary(uid);
        Cart primaryCart = Master.getInstance().getCart(primaryCartId);

        Order order = new Order();
        //set required fields
        order.setUser(primaryCart.getUser());
        order.setCountry(country.toString());
        order.setCurrency(currency.toString());
        //TODO order.setPaymentInstruments();
        //TODO order.setShippingAddressId();

        List<OrderItem> orderItemList = new ArrayList<>();
        List<OfferItem> offerItemList = primaryCart.getOffers();
        for (int i = 0; i < offerItemList.size(); i++) {
            OfferId offerId = offerItemList.get(i).getOffer();

            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(Integer.parseInt(offerItemList.get(i).getQuantity().toString()));
            orderItem.setOffer(offerId);
        }
        order.setOrderItems(orderItemList);

        return orderClient.postOrder(order);
    }

    public String postCreditCardToUser(String uid , CreditCardInfo creditCardInfo) throws Exception{
        PaymentInstrument paymentInstrument = new PaymentInstrument();
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

        Phone phone = new Phone();
        phone.setType(creditCardInfo.getPhone().getType());
        phone.setNumber(creditCardInfo.getPhone().getNumber());

        paymentInstrument.setAccountName(creditCardInfo.getAccountName());
        paymentInstrument.setAccountNum("4111111111111111");
        //paymentInstrument.setAccountNum(creditCardInfo.getAccountNum());
        paymentInstrument.setAddress(address);
        paymentInstrument.setCreditCardRequest(creditCardRequest);
        paymentInstrument.setPhone(phone);
        paymentInstrument.setIsValidated(creditCardInfo.isValidated());
        paymentInstrument.setIsDefault(String.valueOf(creditCardInfo.isDefault()));
        paymentInstrument.setType(creditCardInfo.getType().toString());
        paymentInstrument.setTrackingUuid(UUID.randomUUID());

        return paymentClient.postPaymentInstrumentToUser(uid,paymentInstrument);
    }



}
