package com.junbo.testing.buyerscenario;


import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.user.User;
import com.junbo.testing.buyerscenario.util.BaseTestClass;
import com.junbo.testing.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.property.*;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by Yunlong on 3/20/14.
 */
public class CartCheckout {

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
                    "2  Add digital offer to user's primary cart",
                    "3. Post a new user",
                    "4. Merge the previous anonymous cart to the new user",
                    "4. Checkout the primary cart",
                    "5. Verify the cart is empty",
                     //TODO "7. email validation",
            }
    )
    @Test
    public void testDigitalGoodCheckout() throws Exception {


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
                    "2. Add new credit cart to user",
                    //TODO "Add shipping method to user",
                    "3. Add physical offer into primary cart",
                    "4. Checkout the primary cart",
                    "5. Verify the cart is empty",
                    //TODO "7. email validation",
            }
    )
    @Test
    public void testPhysicalGoodCheckout() throws Exception {


    }

}
