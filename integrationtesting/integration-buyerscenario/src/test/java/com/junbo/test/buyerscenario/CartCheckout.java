package com.junbo.test.buyerscenario;


import com.junbo.test.buyerscenario.util.BaseTestClass;
import com.junbo.test.common.Entities.ShippingAddressInfo;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Entities.paymentInstruments.EwalletInfo;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Yunlong on 3/20/14.
 */
public class CartCheckout extends BaseTestClass {

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
                    "7  Update order tentative to false",
                    "8. Verify Email sent successfully"
            }
    )
    @Test
    public void testDigitalGoodCheckoutByCreditCard() throws Exception {
        String uid = testDataProvider.createUser();

        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);
        offerList.add(offer_digital_normal2);

        //String cartId = testDataProvider.postOffersToPrimaryCart(uid, offerList);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, null, offerList);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        /*
        validationHelper.validateOrderInfoByCartId(
                uid, orderId, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, null);

        testDataProvider.emptyCartByCartId(uid, cartId);
        validationHelper.validateEmailHistory(uid, orderId);
        */
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
                    "7  Update order tentative to false",
                    "8. Verify Email sent successfully"
            }
    )
    @Test
    public void testDigitalGoodCheckoutByEwallet() throws Exception {
        String uid = testDataProvider.createUser();

        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);
        offerList.add(offer_digital_normal2);

        //String cartId = testDataProvider.postOffersToPrimaryCart(uid, offerList);

        EwalletInfo ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.DEFAULT);
        String ewalletId = testDataProvider.postPaymentInstrument(uid, ewalletInfo);

        testDataProvider.creditWallet(uid);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, ewalletId, null, offerList);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        /*
        validationHelper.validateOrderInfoByCartId(
                uid, orderId, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, null);

        testDataProvider.emptyCartByCartId(uid, cartId);

        validationHelper.validateEmailHistory(uid, orderId);
        */
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

        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_physical_normal1);
        offerList.add(offer_physical_normal2);


        ShippingAddressInfo shippingAddressInfo = ShippingAddressInfo.getRandomShippingAddress(Country.DEFAULT);
        String shippingAddressId = testDataProvider.postShippingAddressToUser(uid, shippingAddressInfo);

        //String cartId = testDataProvider.postOffersToPrimaryCart(uid, offerList);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, shippingAddressId, offerList);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        //validationHelper.validateOrderInfoByCartId(
        // uid, orderId, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, shippingAddressId, true);

        //testDataProvider.emptyCartByCartId(uid, cartId);

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

        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_physical_normal1);
        offerList.add(offer_physical_normal2);


        ShippingAddressInfo shippingAddressInfo = ShippingAddressInfo.getRandomShippingAddress(Country.DEFAULT);
        String shippingAddressId = testDataProvider.postShippingAddressToUser(uid, shippingAddressInfo);

        //String cartId = testDataProvider.postOffersToPrimaryCart(uid, offerList);

        EwalletInfo ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.DEFAULT);
        String ewalletId = testDataProvider.postPaymentInstrument(uid, ewalletInfo);

        testDataProvider.creditWallet(uid);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, ewalletId, shippingAddressId, offerList);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        //validationHelper.validateOrderInfoByCartId(
        // uid, orderId, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, shippingAddressId, true);

        //testDataProvider.emptyCartByCartId(uid, cartId);

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

        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_physical_normal1);
        offerList.add(offer_physical_normal2);

        ShippingAddressInfo shippingAddressInfo = ShippingAddressInfo.getRandomShippingAddress(Country.DEFAULT);
        String shippingAddressId = testDataProvider.postShippingAddressToUser(uid, shippingAddressInfo);

        EwalletInfo ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.DEFAULT);
        String ewalletId = testDataProvider.postPaymentInstrument(uid, ewalletInfo);

        testDataProvider.creditWallet(uid, new BigDecimal(20));

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, ewalletId, shippingAddressId, offerList);

        orderId = testDataProvider.updateOrderTentative(orderId, false, 409);

    }

}


