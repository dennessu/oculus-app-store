package com.junbo.test.buyerscenario;


import com.junbo.test.buyerscenario.util.BaseTestClass;
import com.junbo.test.common.Entities.ShippingAddressInfo;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.LogHelper;
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
                    "1. Post a random user",
                    "2. Add digital offer to user's primary cart",
                    "3. Post a new user",
                    "4. Post new credit card to new user.",
                    "5. Merge the previous anonymous cart to the new user",
                    "6. Post order to checkout",
                    "7. Verify the order response info",
                    "8. Empty the primary cart",
                    "9  Update order tentative to false",
                    "10. Verify Email sent successfully"
            }
    )
    @Test
    public void testDigitalGoodCheckout() throws Exception {
        String randomUid = testDataProvider.createUser();

        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);
        offerList.add(offer_digital_normal2);

        testDataProvider.postOffersToPrimaryCart(randomUid, offerList);

        String uid = testDataProvider.createUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postCreditCardToUser(uid, creditCardInfo);

        String cartId = testDataProvider.mergeCart(uid, randomUid);

        String orderId = testDataProvider.postOrderByCartId(
                uid, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, null);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        validationHelper.validateOrderInfoByCartId(
                uid, orderId, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, null);

        testDataProvider.emptyCartByCartId(uid, cartId);

        //TODO CHECK DB
    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test physical good checkout",
            steps = {
                    "1. Post a random user",
                    "2. Add physical offer to user's primary cart",
                    "3. Post a new user",
                    "4. Post new credit card and shipping address to new user.",
                    "5. Merge the previous anonymous cart to the new user",
                    "6. Post order to checkout",
                    "7  Verify order info correct",
                    "8. Empty the primary cart",
                    "9  Update order tentative to false",
                    "10. Verify Email sent successfully"
            }
    )
    @Test
    public void testPhysicalGoodCheckout() throws Exception {
        String randomUid = testDataProvider.createUser();

        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_physical_normal1);
        offerList.add(offer_physical_normal2);

        testDataProvider.postOffersToPrimaryCart(randomUid, offerList);

        String uid = testDataProvider.createUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postCreditCardToUser(uid, creditCardInfo);

        ShippingAddressInfo shippingAddressInfo = ShippingAddressInfo.getRandomShippingAddress(Country.DEFAULT);
        String shippingAddressId = testDataProvider.postShippingAddressToUser(uid, shippingAddressInfo);

        String cartId = testDataProvider.mergeCart(uid, randomUid);

        String orderId = testDataProvider.postOrderByCartId(
                uid, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, shippingAddressId);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        validationHelper.validateOrderInfoByCartId(
                uid, orderId, cartId, Country.DEFAULT, Currency.DEFAULT, creditCardId, shippingAddressId);

        testDataProvider.emptyCartByCartId(uid, cartId);

        //TODO CHECK DB
    }

}


