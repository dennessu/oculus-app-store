/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.endtoend;

import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserTosAgreement;
import com.junbo.test.common.apihelper.identity.impl.UserTosAgreementServiceImpl;
import com.junbo.test.common.apihelper.identity.UserTosAgreementService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.identity.impl.TosServiceImpl;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.TosService;
import com.junbo.test.common.Entities.enums.TosState;
import com.junbo.test.common.Entities.enums.TosType;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.test.common.blueprint.Master;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.identity.spec.v1.model.Tos;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.Date;

/**
 * @author Jason
 * Time: 3/7/2014
 * For holding test cases of identity Tos and UserTosAgreement
 */
public class TosManagement extends TestClass {

    private LogHelper logger = new LogHelper(TosManagement.class);

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Identity,
            owner = "JasonFu",
            status = Status.Disable,
            description = "Test TOS management",
            steps = {
                    "1. Post a default Tos",
                    "2. Post another Tos",
                    "3. Try to get the Tos with the returned ID",
                    "4. Try to get the Tos list",
                    "5. Update the Tos",
                    "6. Delete the Tos"
            }
    )
    @Test
    public void testTosManagement() throws Exception {

        //Post a default Tos
        TosService tosService = TosServiceImpl.instance();
        Tos tosDefault = tosService.postTos();

        Tos tos = new Tos();
        tos.setContent(RandomFactory.getRandomStringOfAlphabetOrNumeric(20));
        tos.setState(TosState.DRAFT.getState());

        tos = tosService.postTos(tos);

        Tos tosRtn1 = tosService.getTos(tosDefault.getId());
        Tos tosRtn2 = tosService.getTos(tos.getId());

        Assert.assertNotNull(tosRtn1);
        Assert.assertNotNull(tosRtn2);

        tosRtn1.setType(TosType.getRandomType().getType());
        tosRtn1.setVersion("version2");
        //Todo currently put Tos doesn't work
        // tosService.putTos(tosRtn1.getId(), tosRtn1);

        tosService.deleteTos(tosRtn1.getId());
        tosService.deleteTos(tosRtn2.getId());

        //verify we can't find the two tos by id
        try {
            tosService.getTos(tosRtn1.getId(), 409);
            Assert.fail("shouldn't find the tos by its id as it has been deleted");
        }
        catch (Exception ex) {
            logger.logInfo("The tos has been deleted and it is expected to catch this exception");
        }

        try {
            tosService.getTos(tosRtn2.getId(), 409);
            Assert.fail("shouldn't find the tos by its id as it has been deleted");
        }
        catch (Exception ex) {
            logger.logInfo("The tos has been deleted and it is expected to catch this exception");
        }
    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Identity,
            owner = "JasonFu",
            status = Status.Disable,
            description = "Test user TOS agreement management",
            steps = {
                    "1. Prepare a user and a tos",
                    "2. Post a user tos agreement(uta)",
                    "3. Try to get the uta with the user ID and uta Id",
                    "4. Try to get the Tos list",
                    "5. Update the Tos",
                    "6. Delete the Tos"
            }
    )
    @Test
    public void testUserTosAgreementManagement() throws Exception {

        //Post a default Tos
        TosService tosService = TosServiceImpl.instance();
        UserTosAgreementService utaService = UserTosAgreementServiceImpl.instance();
        UserService userService = UserServiceImpl.instance();

        //Prepare a user and a tos
        Tos tosDefault = tosService.postTos();
        String userId = userService.PostUser();
        User user = Master.getInstance().getUser(userId);

        UserTosAgreement uta = new UserTosAgreement();
        uta.setTosId(tosDefault.getId());
        //uta.setUserId(user.getId());
        uta.setAgreementTime(new Date());
        uta = utaService.postUserTosAgreement(user.getId(), uta);

        UserTosAgreement utaRtn = utaService.getUserTosAgreement(user.getId(), uta.getId());
        Assert.assertNotNull(utaRtn);

        Results<UserTosAgreement> results = utaService.getUserTosAgreementList(user.getId(), tosDefault.getId());

        Assert.assertEquals(results.getItems().size(), 1);
        Assert.assertEquals(results.getItems().get(0).getTosId(), tosDefault.getId());

        //Put the userTosAgreement: set to use another Tos
        Tos tos2 = tosService.postTos();
        utaRtn.setTosId(tos2.getId());
        //Todo currently put user tos agreement doesn't work
        //utaRtn = utaService.putUserTosAgreement(user.getId(), uta.getId(), utaRtn);
        //Assert.assertNotNull(utaRtn);
        //Assert.assertEquals(utaRtn.getTosId(), tos2.getId());

        //delete
        utaService.deleteUserTosAgreement(user.getId(), uta.getId());

        //verify we can't find the user tos agreement by id
        try {
            utaService.getUserTosAgreement(user.getId(), uta.getId(), 409);
            Assert.fail("shouldn't find the tos by its id as it has been deleted");
        }
        catch (Exception ex) {
            logger.logInfo("The user tos agreement has been deleted and it is expected to catch this exception");
        }

    }

}
