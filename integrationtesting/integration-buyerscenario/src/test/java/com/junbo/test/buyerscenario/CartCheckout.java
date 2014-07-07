package com.junbo.test.buyerscenario;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.test.common.Entities.paymentInstruments.AdyenInfo;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.apihelper.order.impl.OrderEventServiceImpl;
import com.junbo.test.payment.apihelper.impl.PaymentCallbackServiceImpl;
import com.junbo.test.common.Entities.paymentInstruments.EwalletInfo;
import com.junbo.test.common.Entities.paymentInstruments.PayPalInfo;
import com.junbo.test.common.apihelper.order.OrderEventService;
import com.junbo.test.payment.apihelper.PaymentCallbackService;
import com.junbo.payment.spec.model.PaymentCallbackParams;
import com.junbo.test.buyerscenario.util.BaseTestClass;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.test.common.libs.ShardIdHelper;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.order.spec.model.OrderEvent;
import com.junbo.test.common.libs.DBHelper;
import com.junbo.order.spec.model.Order;
import com.junbo.test.common.property.*;
import com.junbo.common.model.Results;
import com.junbo.common.id.UserId;

import net.minidev.json.JSONUtil;
import org.apache.commons.collections.map.HashedMap;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yunlong on 3/20/14.
 */
public class CartCheckout extends BaseTestClass {

    private final String payerId = "CCZA9BJT9NKTS";

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test digital good checkout",
            steps = {
                    "1. Post a new user",
                    "2. Add digital offer to user's primary cart",
                    "3. Post new credit card to new user.",
                    "4. Post order to checkout",
                    "5. Verify the order response info",
                    "6. Empty the primary cart",
                    "7. Update order tentative to false",
                    "8. Get the entitlements by uid",
                    "9. Verify the entitlements are active",
                    "10. Verify Email sent successfully"
            }
    )
    @Test
    public void testDigitalGoodCheckoutByCreditCard() throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_normal1, 1);
        offerList.put(offer_digital_normal2, 1);

        String cartId = testDataProvider.postOffersToPrimaryCart(uid, offerList);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrderByCartId(
                uid, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, null);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        validationHelper.validateOrderInfoByCartId(
                uid, orderId, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, false);
        Results<Entitlement> entitlementResults = testDataProvider.getEntitlementByUserId(uid);

        validationHelper.validateEntitlements(entitlementResults, offerList.size());

        testDataProvider.emptyCartByCartId(uid, cartId);
        //validationHelper.validateEmailHistory(uid, orderId);
    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test digital good checkout",
            steps = {
                    "1. Post a new user",
                    "2. Add digital offer to user's primary cart",
                    "3. Post new ewallet to new user and credit enough money.",
                    "4. Post order to checkout",
                    "5. Verify the order response info",
                    "6. Empty the primary cart",
                    "7. Update order tentative to false",
                    "8. Verify ewallet balance after checkout",
                    "9. Verify Email sent successfully"
            }
    )
    @Test
    public void testDigitalGoodCheckoutByEwallet() throws Exception {
        String uid = testDataProvider.createUser();
        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_storedValue_normal, 1);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        EwalletInfo ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.DEFAULT);
        String ewalletId = testDataProvider.postPaymentInstrument(uid, ewalletInfo);

        String orderId = testDataProvider.postOrder(uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);
        orderId = testDataProvider.updateOrderTentative(orderId, false);
        offerList.clear();


        offerList.put(offer_digital_normal1, 1);
        offerList.put(offer_digital_normal2, 1);

        String cartId = testDataProvider.postOffersToPrimaryCart(uid, offerList);

        orderId = testDataProvider.postOrderByCartId(
                uid, cartId, Country.DEFAULT, Currency.DEFAULT, ewalletId, null);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        validationHelper.validateEwalletBalance(uid, orderId);

        validationHelper.validateOrderInfoByCartId(
                uid, orderId, cartId, Country.DEFAULT, Currency.DEFAULT, ewalletId);

        testDataProvider.emptyCartByCartId(uid, cartId);
        //validationHelper.validateEmailHistory(uid, orderId);
    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test physical good checkout",
            steps = {
                    "1. Post a new user",
                    "2. Add physical offer to user's primary cart",
                    "3. Post new credit card and shipping address to new user.",
                    "4. Post order to checkout",
                    "5  Verify order info correct",
                    "6. Empty the primary cart",
                    "7  Update order tentative to false",
                    "8. Verify Email sent successfully"
            }
    )
    @Test
    public void testPhysicalGoodCheckoutByCreditCard() throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_physical_normal1, 1);
        offerList.put(offer_physical_normal2, 1);

        String cartId = testDataProvider.postOffersToPrimaryCart(uid, offerList);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrderByCartId(
                uid, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, true);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        validationHelper.validateOrderInfoByCartId(
                uid, orderId, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, true);

        testDataProvider.emptyCartByCartId(uid, cartId);
        //validationHelper.validateEmailHistory(uid, orderId);
    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test physical good checkout",
            steps = {
                    "1. Post a new user",
                    "2. Add physical offer to user's primary cart",
                    "3. Post new ewallet to new user and credit enough money",
                    "4. Post order to checkout",
                    "5  Verify order info correct",
                    "6. Empty the primary cart",
                    "7  Update order tentative to false",
                    "8. Verify Email sent successfully"
            }
    )
    @Test
    public void testPhysicalGoodCheckoutByEwallet() throws Exception {
        String uid = testDataProvider.createUser();
        Map<String, Integer> offerList = new HashedMap();


        offerList.put(offer_storedValue_normal, 1);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        EwalletInfo ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.DEFAULT);
        String ewalletId = testDataProvider.postPaymentInstrument(uid, ewalletInfo);

        String orderId = testDataProvider.postOrder(uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, true, offerList);
        testDataProvider.updateOrderTentative(orderId, false);
        offerList.clear();

        offerList.put(offer_physical_normal1, 1);
        offerList.put(offer_physical_normal2, 2);

        String cartId = testDataProvider.postOffersToPrimaryCart(uid, offerList);

        orderId = testDataProvider.postOrderByCartId(
                uid, cartId, Country.DEFAULT, Currency.DEFAULT, ewalletId, true);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        validationHelper.validateEwalletBalance(uid, orderId);

        validationHelper.validateOrderInfoByCartId(
                uid, orderId, cartId, Country.DEFAULT, Currency.DEFAULT, ewalletId, true);

        testDataProvider.emptyCartByCartId(uid, cartId);
        //validationHelper.validateEmailHistory(uid, orderId);
    }

    @Property(
            priority = Priority.Dailies,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test digital good checkout",
            steps = {
                    "1. Post a new user",
                    "2. Add digital offer to user's primary cart",
                    "3. Post new ewallet to new user and credit insufficient money.",
                    "4. Post order to checkout",
                    "5. Verify the order response error",
            }
    )
    @Test
    public void testInsufficientEwalletCheckout() throws Exception {
        String uid = testDataProvider.createUser();
        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_physical_normal1, 1);
        offerList.put(offer_physical_normal2, 1);

        EwalletInfo ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.DEFAULT);
        String ewalletId = testDataProvider.postPaymentInstrument(uid, ewalletInfo);

        testDataProvider.creditWallet(uid, new BigDecimal(20));

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, ewalletId, true, offerList);

        testDataProvider.updateOrderTentative(orderId, false, 409);
    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "JasonFu",
            status = Status.Manual,
            description = "Test digital good checkout by PayPal",
            steps = {
                    "1. Post a new user",
                    "2. Add digital offers to user's primary cart",
                    "3. Post new PayPal info to the user above",
                    "4. Post order to checkout",
                    "5  Update order tentative to false",
                    "6. Mock payPal charge confirm response",
                    "7. Verify the order response info",
                    "8. Verify Email sent successfully"
            }
    )
    @Test
    public void testDigitalGoodsCheckoutByPayPal() throws Exception {
        testCheckoutByPalPal(CatalogItemType.APP);
    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test physical good checkout by PayPal",
            steps = {
                    "1. Post a new user",
                    "2. Add physical offers to user's primary cart",
                    "3. Post new PayPal info to the user above",
                    "4. Post order to checkout",
                    "5  Update order tentative to false",
                    "6. Mock payPal charge confirm response",
                    "7. Verify the order response info"
            }
    )
    @Test
    public void testPhysicalGoodsCheckoutByPayPal() throws Exception {
        testCheckoutByPalPal(CatalogItemType.PHYSICAL);
    }

    private void testCheckoutByPalPal(CatalogItemType itemType) throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();


        switch (itemType) {
            case APP:
                offerList.put(offer_digital_normal1, 1);
                offerList.put(offer_digital_normal2, 1);
                break;
            case PHYSICAL:
                offerList.put(offer_physical_normal1, 1);
                offerList.put(offer_physical_normal2, 1);
                break;
        }

        String cartId = testDataProvider.postOffersToPrimaryCart(uid, offerList);

        PayPalInfo payPalInfo = PayPalInfo.getPayPalInfo(Country.DEFAULT);
        String payPalId = testDataProvider.postPaymentInstrument(uid, payPalInfo);

        String orderId;
        switch (itemType) {
            case APP:
                orderId = testDataProvider.postOrderByCartId(
                        uid, cartId, Country.DEFAULT, Currency.DEFAULT, payPalId, false);
                break;
            case PHYSICAL:
                orderId = testDataProvider.postOrderByCartId(
                        uid, cartId, Country.DEFAULT, Currency.DEFAULT, payPalId, true);
                break;
            default:
                orderId = testDataProvider.postOrderByCartId(
                        uid, cartId, Country.DEFAULT, Currency.DEFAULT, payPalId, true);
                break;
        }

        Order order = Master.getInstance().getOrder(orderId);
        order.setTentative(false);
        order.getPayments().get(0).setSuccessRedirectUrl("http://www.abc.com/");
        order.getPayments().get(0).setCancelRedirectUrl("http://www.abc.com/cancel/");
        orderId = testDataProvider.updateOrder(order);
        order = Master.getInstance().getOrder(orderId);
        String providerConfirmUrl = order.getPayments().get(0).getProviderConfirmUrl();
        int tokenIndex = providerConfirmUrl.indexOf("token=");
        String token = providerConfirmUrl.substring(tokenIndex + 6);

        emulatePayPalCheckout(order, token, itemType);

        //get the order and do verification
        testDataProvider.getOrder(orderId);

        switch (itemType) {
            case PHYSICAL:
                validationHelper.validateOrderInfoByCartId(
                        uid, orderId, cartId, Country.DEFAULT, Currency.DEFAULT, payPalId, true);
                break;
            case APP:
                validationHelper.validateOrderInfoByCartId(
                        uid, orderId, cartId, Country.DEFAULT, Currency.DEFAULT, payPalId, false);
                break;
            default:
                validationHelper.validateOrderInfoByCartId(
                        uid, orderId, cartId, Country.DEFAULT, Currency.DEFAULT, payPalId, false);
                break;
        }
    }

    private void emulatePayPalCheckout(Order order, String token, CatalogItemType itemType) throws Exception {
        //Long paymentTransactionId = getTransactionId(order.getUser().getValue());

        String paymentTransactionId = "";
        //Post callback properties
        //PaymentCallbackParams paymentProperties = new PaymentCallbackParams();
        //paymentProperties.setToken(token);
        //paymentProperties.setPayerID(payerId);
        //PaymentCallbackService paymentCallbackService = PaymentCallbackServiceImpl.getInstance();
        //paymentCallbackService.postPaymentProperties(paymentTransactionId, paymentProperties);

        Map<String, String> properties = new HashMap<>();
        properties.put("paymentId", paymentTransactionId);
        properties.put("payerId", payerId);
        properties.put("token", token);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(properties);

        //Post "charge completed" order event
        OrderEventService orderEventService = OrderEventServiceImpl.getInstance();
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrder(order.getId());
        orderEvent.setAction("CHARGE");
        orderEvent.setStatus("COMPLETED");
        orderEvent.setProperties(json);

        orderEventService.postOrderEvent(orderEvent);
    }

    private void emulateAdyenCheckout(Order order) throws Exception {
        //Long paymentTransactionId = getTransactionId(order.getUser().getValue());
        String paymentTransactionId = "";
        String authResult = "AUTHORISED";
        String pspReference = "";


        PaymentCallbackParams paymentProperties = new PaymentCallbackParams();
        paymentProperties.setAuthResult(authResult);
        paymentProperties.setPspReference(pspReference);
        PaymentCallbackService paymentCallbackService = PaymentCallbackServiceImpl.getInstance();
        paymentCallbackService.postPaymentProperties(paymentTransactionId, paymentProperties);


        //Post "charge completed" order event
        OrderEventService orderEventService = OrderEventServiceImpl.getInstance();
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrder(order.getId());
        orderEvent.setAction("CHARGE");
        orderEvent.setStatus("COMPLETED");

        orderEventService.postOrderEvent(orderEvent);
    }

    private Long getTransactionId(Long uid) throws Exception {
        DBHelper dbHelper = new DBHelper();
        String userId = IdConverter.idToUrlString(UserId.class, uid);

        String sqlStr = String.format(
                "select payment_id from shard_%s.payment where user_id = '%s'",
                ShardIdHelper.getShardIdByUid(userId), uid);

        String paymentId = dbHelper.executeScalar(sqlStr, DBHelper.DBName.PAYMENT);
        return Long.parseLong(paymentId);
    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test digital goods checkout by Adyen",
            steps = {
                    "1. Post a new user",
                    "2. Post new Adyen to the user above",
                    "3. Post order to checkout",

            }
    )
    @Test
    public void testDigitalGoodCheckoutByAdyen() throws Exception {
        String uid = testDataProvider.createUser();

        Country country = Country.DE;
        Currency currency = Currency.EUR;

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_normal1, 1);
        offerList.put(offer_digital_normal2, 2);

        AdyenInfo adyenInfo = AdyenInfo.getAdyenInfo(country);
        String adyenId = testDataProvider.postPaymentInstrument(uid, adyenInfo);

        String orderId = testDataProvider.postOrder(uid, country, currency, adyenId, false, offerList);

        Order order = Master.getInstance().getOrder(orderId);
        order.setTentative(false);
        order.getPayments().get(0).setSuccessRedirectUrl("http://www.baidu.com/");
        order.getPayments().get(0).setCancelRedirectUrl("http://www.baidu.com/cancel/");
        orderId = testDataProvider.updateOrder(order);
        order = Master.getInstance().getOrder(orderId);
        String providerConfirmUrl = order.getPayments().get(0).getProviderConfirmUrl();

        String[] params = providerConfirmUrl.split("&");
        String urlEncoded = new String();

        for (int i = 0; i < params.length; i++) {
            if (params[i].contains("merchantSig")) {
                String sig = params[i].substring(12);
                String sigEncoded = URLEncoder.encode(sig, "utf-8");
                params[i] = "merchantSig=" + sigEncoded;
            }
            if (params[i].contains("shopperEmail")) {
                String email = params[i].substring(13);
                String emailEncoded = URLEncoder.encode(email, "utf-8");
                params[i] = "shopperEmail=" + emailEncoded;
            }
            urlEncoded += params[i] + "&";
        }
        urlEncoded = urlEncoded.substring(0, urlEncoded.length() - 1);

        emulateAdyenCheckout(order);


    }


}


