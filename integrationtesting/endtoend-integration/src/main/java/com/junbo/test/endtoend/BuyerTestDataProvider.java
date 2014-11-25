/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.endtoend;

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
import com.junbo.common.model.Results;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.identity.spec.v1.model.Address;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderItem;
import com.junbo.order.spec.model.PaymentInfo;
import com.junbo.test.billing.utility.BillingTestDataProvider;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.Entities.Identity.UserInfo;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.PaymentInstrumentBase;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.common.apihelper.cart.CartService;
import com.junbo.test.common.apihelper.cart.impl.CartServiceImpl;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.entitlement.EntitlementService;
import com.junbo.test.entitlement.impl.EntitlementServiceImpl;
import com.junbo.test.fulfilment.utility.FulfilmentTestDataProvider;
import com.junbo.test.order.model.enums.EventStatus;
import com.junbo.test.order.model.enums.OrderActionType;
import com.junbo.test.order.utility.OrderTestDataProvider;
import com.junbo.test.payment.apihelper.PaymentService;
import com.junbo.test.payment.apihelper.impl.PaymentServiceImpl;
import com.junbo.test.payment.utility.PaymentTestDataProvider;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Yunlong on 3/20/14.
 */
public class BuyerTestDataProvider extends BaseTestDataProvider {
    protected UserService identityClient = UserServiceImpl.instance();
    protected ItemService itemClient = ItemServiceImpl.instance();
    protected OfferService offerClient = OfferServiceImpl.instance();
    protected CartService cartClient = CartServiceImpl.getInstance();
    protected OrderTestDataProvider orderProvider = new OrderTestDataProvider();
    protected PaymentService paymentClient = PaymentServiceImpl.getInstance();
    protected PaymentTestDataProvider paymentProvider = new PaymentTestDataProvider();
    protected FulfilmentTestDataProvider fulfilmentProvider = new FulfilmentTestDataProvider();
    protected BillingTestDataProvider billingProvider = new BillingTestDataProvider();
    protected OAuthService oAuthProvider = OAuthServiceImpl.getInstance();

    private LogHelper logger = new LogHelper(BuyerTestDataProvider.class);

    public BuyerTestDataProvider() {
    }

    public String getCreditCard(String uid) throws Exception {
        List<String> paymentList = paymentClient.getPaymentInstrumentsByUserId(uid);
        //missing filter payment type only for fraud test
        if (paymentList.size() > 0) {
            return paymentList.get(0);
        } else return null;
    }

    public String createUser() throws Exception {
        return identityClient.PostUser();
    }

    public String createUser(String vat) throws Exception {
        return identityClient.PostUser(vat);
    }

    public String registerUser(UserInfo userInfo) throws Exception {
        return identityClient.RegisterUser(userInfo, 200);
    }

    public String getEmailVerificationLinks(String cid) throws Exception {
        return identityClient.GetEmailVerificationLinks(cid);
    }

    public void accessEmailVerificationLinks(String emailVerifyLink) throws Exception {
        if (emailVerifyLink != null && !emailVerifyLink.isEmpty()) {
            oAuthProvider.accessEmailVerifyLink(emailVerifyLink);
        }
    }

    public String BindUserPersonalInfos(UserInfo userInfo) throws Exception {
        return identityClient.BindUserPersonalInfos(userInfo);
    }

    public String createUser(String userName, String emailAddress) throws Exception {
        List<String> userList = identityClient.GetUserByUserName(userName);
        if (userList.size() > 0) {
            return userList.get(0);
        }

        return identityClient.PostUser(userName, emailAddress);
    }

    public String createUser(String vatId, Address address) throws Exception {
        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        return identityClient.PostUser(vatId, addresses);
    }

    public String createUser(String vatId, List<Address> address) throws Exception {
        return identityClient.PostUser(vatId, address);
    }

    public String getUserByUid(String userId) throws Exception {
        return identityClient.GetUserByUserId(userId);
    }

    public String putUser(String uid, User user) throws Exception {
        return identityClient.PutUser(uid, user);
    }

    public String getUserByUserName(String userName) throws Exception {
        return identityClient.GetUserByUserName(userName).get(0);
    }

    public String getUserByUserName(String userName, String pwd) throws Exception {
        return identityClient.GetUserByUserName(userName).get(0);
    }

    public String postDefaultItem() throws Exception {
        Item item = itemClient.postDefaultItem(CatalogItemType.PHYSICAL);
        return IdConverter.idToUrlString(ItemId.class, item.getItemId());
    }

    public String postDefaultOffer(CatalogItemType itemType) throws Exception {
        logger.LogSample("Post a offer");
        Offer offer = offerClient.postDefaultOffer();
        return IdConverter.idToUrlString(OfferId.class, offer.getOfferId());
    }

    public String postOffersToPrimaryCart(String uid, Map<String, Integer> offers) throws Exception {
        String primaryCartId = cartClient.getCartPrimary(uid);
        Cart primaryCart = Master.getInstance().getCart(primaryCartId);
        List<OfferItem> offerItemList = new ArrayList<>();

        Set<String> key = offers.keySet();
        for (Iterator it = key.iterator(); it.hasNext(); ) {
            OfferItem offerItem = new OfferItem();
            String offerName = (String) it.next();
            OfferId offerId = new OfferId(offerClient.getOfferIdByName(offerName));
            offerItem.setQuantity(new Long(offers.get(offerName).toString()));
            offerItem.setIsSelected(true);
            offerItem.setOffer(offerId);
            offerItem.setIsApproved(true);
            offerItemList.add(offerItem);
        }

        primaryCart.setOffers(offerItemList);
        Master.getInstance().setCurrentUid(uid);
        Master.getInstance().addCart(primaryCartId, primaryCart);
        return cartClient.updateCart(uid, primaryCartId, primaryCart);
    }


    public String postPaymentInstrument(String uid, PaymentInstrumentBase paymentInfo) throws Exception {
        return paymentProvider.postPaymentInstrument(uid, paymentInfo);
    }

    public String postPaymentInstrument(String uid, PaymentInstrumentBase paymentInfo, long billAddressId) throws Exception {
        return paymentProvider.postPaymentInstrument(uid, paymentInfo, billAddressId);
    }

    public void deletePaymentInstrument(String uid, String paymentId) throws Exception
    {
        paymentProvider.deletePaymentInstruments(uid, paymentId);
    }

    public void creditWallet(String uid) throws Exception {
        paymentProvider.creditWallet(uid);
    }

    public void creditWallet(String uid, BigDecimal amount) throws Exception {
        paymentProvider.creditWallet(uid, amount);
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
                            boolean hasPhysicalGood, Map<String, Integer> offers) throws Exception {

        return this.postOrder(uid, country, currency, paymentInstrumentId, hasPhysicalGood, offers, 200);
    }

    public String postOrder(String uid, Country country, Currency currency, String paymentInstrumentId,
                            boolean hasPhysicalGood, long shippingAddressId, Map<String, Integer> offers)
            throws Exception {

        return orderProvider.postOrder(uid, country, currency, paymentInstrumentId, hasPhysicalGood,
                shippingAddressId, offers, 200);
    }

    public String postOrder(String uid, Country country, Currency currency, String paymentInstrumentId,
                            boolean hasPhysicalGood, Map<String, Integer> offers, int expectedResponseCode)
            throws Exception {

        return orderProvider.postOrder(uid, country, currency, paymentInstrumentId, hasPhysicalGood, offers, expectedResponseCode);
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
        if (paymentInstrumentId != null) {
            PaymentInstrumentId pid = new PaymentInstrumentId();
            pid.setValue(IdConverter.hexStringToId(PaymentInstrumentId.class, paymentInstrumentId));
            paymentInfo.setPaymentInstrument(pid);
        }
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
            order.setShippingMethod("1L");
            order.setShippingAddress(Master.getInstance().getUser(uid).getAddresses().get(0).getValue());
            order.setShippingToName(Master.getInstance().getUser(uid).getName());
            //order.setShippingToPhone(Master.getInstance().getUser(uid).getPhones().get(0).getValue());
        }
        order.setOrderItems(orderItemList);
        order.setTentative(true);

        order.setLocale(new LocaleId("en_US"));
        logger.LogSample("Post an order");
        return orderProvider.postOrder(order);
    }


    public String updateOrderTentative(String orderId, boolean isTentative) throws Exception {
        return this.updateOrderTentative(orderId, isTentative, 200);
    }

    public String updateOrderTentative(String orderId, boolean isTentative, int expectedResponseCode) throws Exception {
        return orderProvider.updateOrderTentative(orderId, isTentative, expectedResponseCode);
    }

    public void emptyCartByCartId(String uid, String cartId) throws Exception {
        if (cartId == null) {
            cartId = cartClient.getCartPrimary(uid);
        }
        logger.LogSample("Put cart");
        Cart cart = new Cart();
        cart.setRev(new String(Master.getInstance().getCart(cartId).getRev()));
        cartClient.updateCart(uid, cartId, cart);
    }

    public String updateOrder(Order order) throws Exception {
        return orderProvider.updateOrder(order);
    }

    public String getOrder(String orderId) throws Exception {
        return orderProvider.getOrder(orderId);
    }

    public Results<Entitlement> getEntitlementByUserId(String uid) throws Exception {
        EntitlementService entitlementService = EntitlementServiceImpl.instance();
        return entitlementService.getEntitlements(uid);
    }

    public void getBinariesUrl(Entitlement entitlement) throws Exception {
        EntitlementService entitlementService = EntitlementServiceImpl.instance();
        entitlementService.getBinariesUrl(entitlement);
    }

    public String getFulfilmentsByOrderId(String orderId) throws Exception {
        return fulfilmentProvider.getFulfilmentByOrderId(orderId);
    }

    public List<String> getBalancesByOrderId(String orderId) throws Exception {
        return billingProvider.getBalancesByOrderId(orderId);
    }

    public void postOrderEvent(String orderId, EventStatus eventStatus, OrderActionType orderActionType)
            throws Exception {
        orderProvider.postOrderEvent(orderId, eventStatus, orderActionType);
    }

}
