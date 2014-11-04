/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.buyerscenario;

import com.junbo.common.enumid.LocaleId;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.test.buyerscenario.util.BaseTestClass;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.Identity.UserInfo;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;

/**
 * Created by weiyu_000 on 10/11/14.
 */
public class RouteTesting extends BaseTestClass {

    LogHelper logHelper = new LogHelper(RouteTesting.class);

    @Property(
            priority = Priority.Dailies,
            features = "RouteTesting",
            component = Component.Identity,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            environment = "release",
            description = "update nickname route testing",
            steps = {
                    "1. Register user against the first endpoint",
                    "2  Access email verification links against the second endpoint",
                    "3. Get user against the second endpoint",
                    "4. Update user user's preferred locale against the first endpoint",
                    "5. Sleep 3 seconds",
                    "6. Get user against the second endpoint",
                    "7. Verify preferred locale should be updated",
            }
    )
    @Test
    public void testUpdateUserRoute() throws Exception {
        if (ConfigHelper.getSetting("secondaryDcEndpoint") == null) return;
        UserInfo userInfo = UserInfo.getRandomUserInfo();
        String cid = testDataProvider.registerUser(userInfo);
        String uid = testDataProvider.BindUserPersonalInfos(userInfo);
        String emailLinks = testDataProvider.getEmailVerificationLinks(cid);
        Master.getInstance().setEndPointType(Master.EndPointType.Secondary);
        testDataProvider.accessEmailVerificationLinks(emailLinks);
        testDataProvider.getUserByUid(uid);
        Master.getInstance().setEndPointType(Master.EndPointType.Primary);

        User user = Master.getInstance().getUser(uid);
        user.setPreferredLocale(new LocaleId("en_CA"));
        Thread.sleep(3000);
        testDataProvider.putUser(uid, user);

        Thread.sleep(3000);
        Master.getInstance().setEndPointType(Master.EndPointType.Secondary);
        testDataProvider.getUserByUid(uid);

        user = Master.getInstance().getUser(uid);
        assert user.getPreferredLocale().getValue().equals("en_CA");

    }

}
