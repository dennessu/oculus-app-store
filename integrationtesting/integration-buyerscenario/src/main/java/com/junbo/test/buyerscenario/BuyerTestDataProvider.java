/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.buyerscenario;

import com.junbo.billing.spec.model.ShippingAddress;
import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.order.spec.model.OrderItem;
import com.junbo.payment.spec.model.Address;
import com.junbo.payment.spec.model.CreditCardRequest;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.test.billing.apihelper.ShippingAddressService;
import com.junbo.test.billing.apihelper.impl.ShippingAddressServiceImpl;
import com.junbo.test.common.Entities.ShippingAddressInfo;
import com.junbo.test.common.Utility.BaseTestDataProvider;

import com.junbo.test.common.apihelper.cart.CartService;
import com.junbo.test.common.apihelper.cart.impl.CartServiceImpl;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.common.apihelper.catalog.OfferService;
import com.junbo.test.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.test.common.apihelper.catalog.impl.OfferServiceImpl;
import com.junbo.order.spec.model.Order;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.order.OrderService;
import com.junbo.test.common.apihelper.order.impl.OrderServiceImpl;

import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.EnumHelper.UserStatus;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.payment.apihelper.PaymentService;
import com.junbo.test.payment.apihelper.impl.PaymentServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Yunlong on 3/20/14.
 */
public class BuyerTestDataProvider extends BaseTestDataProvider{
    private UserService identityClient = UserServiceImpl.instance();
    private ItemService itemClient = ItemServiceImpl.instance();
    private OfferService offerClient = OfferServiceImpl.instance();
    private CartService cartClient = CartServiceImpl.getInstance();
    private OrderService orderClient = OrderServiceImpl.getInstance();
    private PaymentService paymentClient = PaymentServiceImpl.getInstance();
    private ShippingAddressService shippingClient = ShippingAddressServiceImpl.getInstance();

    private LogHelper logger = new LogHelper(BuyerTestDataProvider.class);


    public BuyerTestDataProvider() {
    }

    public String createUser(String email, String password, UserStatus status) throws Exception {
        User userToPost = new User();
        //userToPost.setUserName(email);
        //userToPost.setPassword(password);
        //userToPost.setStatus(status.getStatus());
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
        return itemClient.postDefaultItem(EnumHelper.CatalogItemType.PHYSICAL);
    }

    public String postDefaultOffer(EnumHelper.CatalogItemType itemType) throws Exception {
        logger.LogSample("Post a offer");
        return offerClient.postDefaultOffer(itemType);
    }

    public String postOffersToPrimaryCart(String uid, ArrayList<String> offers) throws Exception {
        String primaryCartId = cartClient.getCartPrimary(uid);
        Cart primaryCart = Master.getInstance().getCart(primaryCartId);
        List<OfferItem> offerItemList = new ArrayList<>();
        List<String> couponItemList = new ArrayList<>();
        for (int i = 0; i < offers.size(); i++) {
            OfferItem offerItem = new OfferItem();
            offerItem.setQuantity(RandomFactory.getRandomLong(1L, 5L));
            offerItem.setSelected(true);
            OfferId offerId = new OfferId(
                    IdConverter.hexStringToId(OfferId.class, offerClient.getOfferIdByName(offers.get(i))));
            offerItem.setOffer(offerId);

            offerItemList.add(offerItem);
        }
        primaryCart.setOffers(offerItemList);
        primaryCart.setCouponCodes(couponItemList);

        Master.getInstance().addCart(primaryCartId, primaryCart);
        return cartClient.updateCart(uid, primaryCartId, primaryCart);
    }

    public String postDefaultOffersToPrimaryCart(String uid, EnumHelper.CatalogItemType itemType) throws Exception {
        String offerId = this.postDefaultOffer(itemType);
        //String offerId = IdConverter.idLongToHexString(OfferId.class, new OfferId(100001L).getValue());
        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offerId);
        return this.postOffersToPrimaryCart(uid, offerList);
    }

    public String mergeCart(String destinationUid, String sourceUid) throws Exception {
        String sourceCartId = cartClient.getCartPrimary(sourceUid);
        Cart sourceCart = Master.getInstance().getCart(sourceCartId);

        String destinationCartId = cartClient.getCartPrimary(destinationUid);
        //Cart destinationCart = Master.getInstance().getCart(destinationCartId);
        logger.LogSample("Merge the carts");
        return cartClient.mergeCart(destinationUid, destinationCartId, sourceCart);
    }

    public String postCreditCardToUser(String uid, CreditCardInfo creditCardInfo) throws Exception {
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

        ArrayList<Long> admins = new ArrayList<>();
        admins.add(IdConverter.hexStringToId(UserId.class,uid));
        paymentInstrument.setAccountName(creditCardInfo.getAccountName());
        paymentInstrument.setAccountNum(creditCardInfo.getAccountNum());
        //paymentInstrument.setAccountNum(creditCardInfo.getAccountNum());
        paymentInstrument.setAddress(address);
        paymentInstrument.setCreditCardRequest(creditCardRequest);
        paymentInstrument.setIsValidated(creditCardInfo.isValidated());
        paymentInstrument.setPhoneNum("650-253-0000");
        paymentInstrument.setType(creditCardInfo.getType().toString());
        paymentInstrument.setTrackingUuid(UUID.randomUUID());
        logger.LogSample("Post a new credit card to user");
        return paymentClient.postPaymentInstrumentToUser(uid, paymentInstrument);
    }

    public String postShippingAddressToUser(String uid, ShippingAddressInfo shippingAddressInfo) throws Exception {
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
    }

    public String postOrderByCartId(String uid, String cartId, Country country, Currency currency,
                                    String paymentInstrumentId) throws Exception {
        return postOrderByCartId(uid, cartId, country, currency, paymentInstrumentId, null);
    }

    public String postOrderByCartId(String uid, String cartId, Country country, Currency currency,
                                    String paymentInstrumentId, String shippingAddressId) throws Exception {
        if (cartId == null) {
            cartId = cartClient.getCartPrimary(uid);
        }

        Cart cart = Master.getInstance().getCart(cartId);
        Order order = new Order();

        List<PaymentInstrumentId> paymentInstruments = new ArrayList<>();
        paymentInstruments.add(new PaymentInstrumentId(
                IdConverter.hexStringToId(PaymentInstrumentId.class, paymentInstrumentId)));
        order.setUser(cart.getUser());
        order.setCountry(country.toString());
        order.setCurrency(currency.toString());
        order.setPaymentInstruments(paymentInstruments);
        if (shippingAddressId != null) {
            order.setShippingAddress(Master.getInstance().getShippingAddress(shippingAddressId).getAddressId());
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        List<OfferItem> offerItemList = cart.getOffers();
        for (int i = 0; i < offerItemList.size(); i++) {
            OfferId offerId = offerItemList.get(i).getOffer();

            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(Integer.parseInt(offerItemList.get(i).getQuantity().toString()));
            orderItem.setOffer(offerId);
            orderItemList.add(orderItem);
        }
        order.setOrderItems(orderItemList);
        order.setTentative(true);
        order.setType("PAY_IN");
        order.setLocale("en_US");
        logger.LogSample("Post an order");
        return orderClient.postOrder(order);
    }

    public String updateOrderTentative(String orderId, boolean isTentative) throws Exception {
        Order order = Master.getInstance().getOrder(orderClient.getOrderByOrderId(orderId));
        order.setTentative(isTentative);
        logger.LogSample("Put an order");
        return orderClient.updateOrder(order);
    }

    public void emptyCartByCartId(String uid, String cartId) throws Exception {
        if (cartId == null) {
            cartId = cartClient.getCartPrimary(uid);
        }
        logger.LogSample("Put cart");
        cartClient.updateCart(uid, cartId, new Cart());
    }

}
