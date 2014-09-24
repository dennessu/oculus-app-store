package com.junbo.test.buyerscenario;

import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.order.model.enums.EventStatus;
import com.junbo.test.order.model.enums.OrderActionType;
import com.junbo.test.common.Entities.paymentInstruments.EwalletInfo;
import com.junbo.test.common.Entities.paymentInstruments.PayPalInfo;
import com.junbo.test.common.Entities.paymentInstruments.AdyenInfo;
import com.junbo.test.buyerscenario.util.BaseTestClass;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.test.common.libs.ShardIdHelper;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.DBHelper;
import com.junbo.test.common.property.*;
import com.junbo.common.model.Results;
import com.junbo.common.id.UserId;

import org.apache.commons.collections.map.HashedMap;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

        testDataProvider.postOrderEvent(orderId, EventStatus.COMPLETED, OrderActionType.FULFILL);

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

        testDataProvider.updateOrderTentative(orderId, false, 412);
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
            status = Status.Manual,
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

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "JasonFu",
            status = Status.Manual,
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

        testDataProvider.orderProvider.getProviderConfirmUrl(orderId);

        testDataProvider.orderProvider.emulateAdyenCallBack(orderId);
    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test checkout",
            steps = {
                    "1. Post a new user",
                    "3. Post new credit card to new user.",
                    "4. Post another user",
                    "5. Post order with the first credit card ",
                    "6. Update order tentative to false",
                    "7. Verify restriction"
            }
    )
    @Test
    public void testCheckoutWithAnotherCreditCard() throws Exception {
        String uid1 = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_normal1, 1);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid1, creditCardInfo);

        String uid2 = testDataProvider.createUser();

        testDataProvider.postOrder(
                uid2, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList, 500);


    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            environment = "release",
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test checkout free offer",
            steps = {
                    "1. Post a new user",
                    "2. Add free digital offer to user's primary cart",
                    "3. Post new credit card to new user.",
                    "4. Post order to checkout",
                    "5. Verify the order response info",
                    "6. Empty the primary cart",
                    "7. Update order tentative to false",
                    "8. Get the entitlements by uid",
                    "9. Verify the entitlements are active",
            }
    )
    @Test
    public void testCheckoutFreeDigital() throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_free, 1);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.FREE, null, false, offerList);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        validationHelper.validateFreeOrderInfo(uid, orderId, Country.DEFAULT, Currency.FREE, false);

    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            environment = "release",
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test checkout free offer with multi endpoint",
            steps = {
                    "1. Post a new user in west dc",
                    "2. Add free digital offer to user's primary cart",
                    "3. Post new credit card to new user.",
                    "4. Post order to checkout",
                    "5. Verify the order response info",
                    "6. Empty the primary cart",
                    "7. Update order tentative to false in east dc",
                    "8. Get the entitlements by uid ",
                    "9. Verify the entitlements are active",
            }
    )
    @Test(groups = "int/ppe/prod/sewer")
    public void testCheckoutFreeDigitalWithMultiEndpoint() throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_free, 1);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.FREE, null, false, offerList);

        Master.getInstance().setEndPointType(Master.EndPointType.Secondary);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        validationHelper.validateFreeOrderInfo(uid, orderId, Country.DEFAULT, Currency.FREE, false);

    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            environment = "release",
            owner = "ZhaoYunlong",
            status = Status.Disable,
            description = "Test checkout free offer",
            steps = {
                    "1. Post a new user",
                    "2. Add free physical offer to user's primary cart",
                    "3. Post new credit card to new user.",
                    "4. Post order to checkout",
                    "5. Verify the order response info",
                    "6. Empty the primary cart",
                    "7. Update order tentative to false",
                    "8. Get the entitlements by uid",
                    "9. Verify the entitlements are active",
            }
    )
    @Test
    public void testCheckoutFreePhysical() throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_physical_free, 1);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.FREE, null, true, offerList);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        validationHelper.validateFreeOrderInfo(uid, orderId, Country.DEFAULT, Currency.FREE, true);

    }


    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Disable,
            description = "Prepare orders for CSR",
            steps = {
                    "1. Post a new user",
                    "2. Post a credit card to user",
                    "3. Post order with all kinds of offers",

            }
    )
    @Test
    public void prepareCSRData() throws Exception {
        Master.getInstance().setUserPassword(String.format("csradmin"));
        String uid = testDataProvider.getUserByUserName(String.format("csradmin"));

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_normal1, 1);
        offerList.put(offer_digital_normal2, 1);
        offerList.put(offer_physical_normal1, 1);
        offerList.put(offer_physical_normal2, 1);
        offerList.put(offer_storedValue_normal, 1);
        offerList.put(offer_inApp_consumable1, 2);
        offerList.put(offer_inApp_consumable2, 2);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        EwalletInfo ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.DEFAULT);
        testDataProvider.postPaymentInstrument(uid, ewalletInfo);

        Set<String> offerNames = offerList.keySet();

        for (Iterator it = offerNames.iterator(); it.hasNext(); ) {
            String offerName = (String) it.next();
            Map<String, Integer> offers = new HashMap<>();
            offers.put(offerName, offerList.get(offerName));
            boolean hasPhysical = offerName.toLowerCase().contains("physical");
            String orderId = testDataProvider.postOrder(
                    uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, hasPhysical, offers);
            testDataProvider.updateOrderTentative(orderId, false);
        }
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

        String token = testDataProvider.orderProvider.getPaypalToken(orderId);

        testDataProvider.orderProvider.emulatePaypalCallback(orderId, token);

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

    private Long getTransactionId(Long uid) throws Exception {
        DBHelper dbHelper = new DBHelper();
        String userId = IdConverter.idToUrlString(UserId.class, uid);

        String sqlStr = String.format(
                "select payment_id from shard_%s.payment where user_id = '%s'",
                ShardIdHelper.getShardIdByUid(userId), uid);

        String paymentId = dbHelper.executeScalar(sqlStr, DBHelper.DBName.PAYMENT);
        return Long.parseLong(paymentId);
    }


}


