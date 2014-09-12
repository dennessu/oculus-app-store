/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.buyerscenario;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserLoginName;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.test.buyerscenario.util.BaseTestClass;
import com.junbo.test.common.Entities.Identity.UserInfo;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Entities.paymentInstruments.EwalletInfo;
import com.junbo.test.common.Entities.paymentInstruments.PayPalInfo;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.identity.Identity;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;


/**
  * @author Jason
  * Time: 3/7/2014
  * For holding test cases of User portal
*/
public class UserPortal extends BaseTestClass {

    private LogHelper logger = new LogHelper(UserPortal.class);

    @Property(
            priority = Priority.BVT,
            features = "CustomerScenarios",
            component = Component.Identity,
            owner = "JasonFu",
            status = Status.Disable,
            description = "Test post user",
            steps = {
                    "1. Post a user and get its user ID",
                    "2. Try to get the user with the returned ID",
                    "3. Try to get the user with the returned username"
            }
    )
    @Test
    public void testPostUser() throws Exception {

        UserService us = UserServiceImpl.instance();
        String userPostId = us.PostUser();

        Assert.assertNotNull(Master.getInstance().getUser(userPostId));
        Assert.assertNotNull(Master.getInstance().getUser(userPostId).getUsername());

        //Get the user with ID
        userPostId = us.GetUserByUserId(userPostId);
        Assert.assertNotNull(userPostId, "Can't get user by user ID");

        //Get the user with userName
        UserPersonalInfo loginName = Identity.UserPersonalInfoGetByUserPersonalInfoId(Master.getInstance().getUser(userPostId).getUsername());
        UserLoginName userLoginName = ObjectMapperProvider.instance().treeToValue(loginName.getValue(), UserLoginName.class);
        List<String> userGetList = us.GetUserByUserName(userLoginName.getUserName());
        Assert.assertNotNull(userGetList, "Can't get user by user Name");
    }

    @Property(
            priority = Priority.Dailies,
            features = "CustomerScenarios",
            component = Component.Identity,
            owner = "JasonFu",
            status = Status.Enable,
            environment = "release",
            description = "Test post user",
            steps = {
                    "1. Post a user and get its user ID",
                    "2. Put a user with default PI"
            }
    )
    @Test
    // Fix https://oculus.atlassian.net/browse/SER-368
    public void testPostUserDefaultPI() throws Exception {
        UserInfo randomUser = UserInfo.getRandomUserInfo();
        UserService us = UserServiceImpl.instance();
        String userPostId = us.PostUser(randomUser);

        Assert.assertNotNull(Master.getInstance().getUser(userPostId));
        Assert.assertNotNull(Master.getInstance().getUser(userPostId).getUsername());

        User user = Master.getInstance().getUser(userPostId);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(userPostId, creditCardInfo);

        user.setDefaultPI(new PaymentInstrumentId(IdConverter.hexStringToId(PaymentInstrumentId.class, creditCardId)));
        String userPutId = us.PutUser(userPostId, user);
        assert userPostId.equalsIgnoreCase(userPutId);

        PayPalInfo payPalInfo = PayPalInfo.getPayPalInfo(Country.DEFAULT);
        String paypayId = testDataProvider.postPaymentInstrument(userPutId, payPalInfo);
        user = Master.getInstance().getUser(userPutId);

        user.setDefaultPI(new PaymentInstrumentId(IdConverter.hexStringToId(PaymentInstrumentId.class, paypayId)));
        com.junbo.common.error.Error error = us.PutUserWithError(userPutId, user, 400, "131.001");
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase("defaultPI");
        assert error.getDetails().get(0).getReason().contains("Field value is invalid.");

        EwalletInfo ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.DEFAULT);
        String ewalletInfoId = testDataProvider.postPaymentInstrument(userPutId, ewalletInfo);
        user = Master.getInstance().getUser(userPutId);

        user.setDefaultPI(new PaymentInstrumentId(IdConverter.hexStringToId(PaymentInstrumentId.class, ewalletInfoId)));
        userPutId = us.PutUser(userPutId, user);
        assert userPutId.equalsIgnoreCase(userPostId);

        List<String> userIds = us.GetCurrentUserByUserName(randomUser.getUserName(), 403);
        assert userIds == null;
    }
}
