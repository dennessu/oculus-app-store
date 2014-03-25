package com.junbo.testing.buyerscenario;


import com.junbo.testing.buyerscenario.util.BaseTestClass;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.property.*;

import org.testng.Assert;
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
            status = Status.Enable,
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
        testDataProvider.postDefaultOffersToPrimaryCart(randomUid);

        String uid = testDataProvider.createUser();
        testDataProvider.mergeCart(uid, randomUid);


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
        testDataProvider.postDefaultOffersToPrimaryCart(randomUid);

        String uid = testDataProvider.createUser();
        testDataProvider.mergeCart(uid, randomUid);

    }

}
