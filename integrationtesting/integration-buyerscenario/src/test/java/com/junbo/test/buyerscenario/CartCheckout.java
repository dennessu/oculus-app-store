package com.junbo.test.buyerscenario;


import com.junbo.test.buyerscenario.util.BaseTestClass;
import com.junbo.test.common.Entities.ShippingAddressInfo;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;

/**
 * Created by Yunlong on 3/20/14.
 */
public class CartCheckout extends BaseTestClass {

    private LogHelper logger = new LogHelper(CartCheckout.class);

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Incomplete,
            description = "Test digital good checkout",
            steps = {
                    "1. Post a random user",
                    "2. Add digital offer to user's primary cart",
                    "3. Post a new user",
                    "4. Post new credit card to new user.",
                    "5. Merge the previous anonymous cart to the new user",
                    "6. Post order to checkout",
                    "7. Close the primary cart",
                    "8. Verify the primary cart is empty",
                    //TODO "9. email validation",
            }
    )
    @Test
    public void testDigitalGoodCheckout() throws Exception {
        String randomUid = testDataProvider.createUser();

        testDataProvider.postDefaultOffersToPrimaryCart(randomUid, false);

        String uid = testDataProvider.createUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        testDataProvider.postCreditCardToUser(uid,creditCardInfo);

        testDataProvider.mergeCart(uid, randomUid);


    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Incomplete,
            description = "Test physical good checkout",
            steps = {
                    "1. Post a random user",
                    "2. Add physical offer to user's primary cart",
                    "3. Post a new user",
                    "4. Post new credit card and shipping method to new user.",
                    "5. Merge the previous anonymous cart to the new user",
                    "6. Post order to checkout",
                    "7. Close the primary cart",
                    "8. Verify the primary cart is empty",
                    //TODO "9. email validation",
            }
    )
    @Test
    public void testPhysicalGoodCheckout() throws Exception {
        String randomUid = testDataProvider.createUser();
        testDataProvider.postDefaultOffersToPrimaryCart(randomUid, false);

        String uid = testDataProvider.createUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        testDataProvider.postCreditCardToUser(uid,creditCardInfo);

        ShippingAddressInfo shippingAddressInfo = ShippingAddressInfo.getRandomShippingAddress(Country.DEFAULT);
        testDataProvider.postShippingAddressToUser(uid, shippingAddressInfo);

        testDataProvider.mergeCart(uid, randomUid);

    }

}
