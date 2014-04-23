package com.junbo.test.buyerscenario;


import com.junbo.test.buyerscenario.util.BaseTestClass;
import com.junbo.test.common.Entities.ShippingAddressInfo;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;

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
    public void testDigitalGoodCheckout() throws Exception {
        //String uid = testDataProvider.createUser();
        String uid = "6B54FD213C9F";

        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);
        offerList.add(offer_digital_normal2);

        String cartId = testDataProvider.postOffersToPrimaryCart(uid, offerList);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        //String creditCardId = testDataProvider.postCreditCardToUser(uid, creditCardInfo);
        String creditCardId = "6B54FF0CBC9F";


        String orderId = testDataProvider.postOrderByCartId(
                uid, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, null);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        /*
        validationHelper.validateOrderInfoByCartId(
                uid, orderId, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, null);
         */

        testDataProvider.emptyCartByCartId(uid, cartId);

        validationHelper.validateEmailHistory(uid, orderId);
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
    public void testPhysicalGoodCheckout() throws Exception {
        String uid = testDataProvider.createUser();
        //String uid = "6B54FD213C9F";

        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_physical_normal1);
        offerList.add(offer_physical_normal2);

        String cartId = testDataProvider.postOffersToPrimaryCart(uid, offerList);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postCreditCardToUser(uid, creditCardInfo);
        //String creditCardId = "6B54FF0CBC9F";

        ShippingAddressInfo shippingAddressInfo = ShippingAddressInfo.getRandomShippingAddress(Country.DEFAULT);
        String shippingAddressId = testDataProvider.postShippingAddressToUser(uid, shippingAddressInfo);

        String orderId = testDataProvider.postOrderByCartId(
                uid, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, shippingAddressId, true);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        validationHelper.validateOrderInfoByCartId(
                uid, orderId, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, shippingAddressId, true);

        testDataProvider.emptyCartByCartId(uid, cartId);

        validationHelper.validateEmailHistory(uid, orderId);
    }

}


