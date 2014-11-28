/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.csr;

import com.junbo.csr.spec.model.CsrUpdate;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by weiyu_000 on 11/25/14.
 */
public class CsrUpdateTesting extends CsrBaseTestClass {

    @Property(
            priority = Priority.BVT,
            features = "CSR Group",
            component = Component.CSR,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test create csr update",
            steps = {
                    "1. Post csr update",
                    "2. verify response"

            }
    )
    @Test
    public void testCreateCsrUpdate() throws Exception {
        String at = testDataProvider.postCsrAdminAccessToken();
        Master.getInstance().setCurrentUid(csrAdminUid);

        String uid = testDataProvider.getCsrAdminUid(at);

        String randomString = RandomFactory.getRandomStringOfAlphabet(100);
        CsrUpdate csrUpdate = testDataProvider.createCsrUpdate(uid, randomString);

        validationHelper.validateCsrUpdate(uid, randomString, csrUpdate);
    }

    @Property(
            priority = Priority.BVT,
            features = "CSR Group",
            component = Component.CSR,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test get csr update",
            steps = {
                    "1. Post csr update",
                    "2. Get csr update by update id",
                    "2. verify response"

            }
    )
    @Test
    public void testGetCsrUpdate() throws Exception {
        String at = testDataProvider.postCsrAdminAccessToken();
        Master.getInstance().setCurrentUid(csrAdminUid);

        String uid = testDataProvider.getCsrAdminUid(at);

        String randomString = RandomFactory.getRandomStringOfAlphabet(100);
        CsrUpdate update = testDataProvider.createCsrUpdate(uid, randomString);

        CsrUpdate csrUpdate = testDataProvider.getCsrUpdate(IdConverter.idToHexString(update.getId()));

        validationHelper.validateCsrUpdate(uid, randomString, csrUpdate);
    }

    @Property(
            priority = Priority.BVT,
            features = "CSR Group",
            component = Component.CSR,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test patch csr update",
            steps = {
                    "1. Post csr update",
                    "2. Patch csr update by update id",
                    "2. verify response"

            }
    )
    @Test
    public void testPatchCsrUpdate() throws Exception {
        String at = testDataProvider.postCsrAdminAccessToken();
        Master.getInstance().setCurrentUid(csrAdminUid);

        String uid = testDataProvider.getCsrAdminUid(at);

        String randomString1 = RandomFactory.getRandomStringOfAlphabet(100);
        CsrUpdate update = testDataProvider.createCsrUpdate(uid, randomString1);

        String randomString2 = RandomFactory.getRandomStringOfAlphabet(100);
        String updateId = IdConverter.idToHexString(update.getId());

        CsrUpdate csrUpdate = testDataProvider.patchCsrUpdate(updateId, uid, randomString2);

        String expectedContent = randomString1.concat(randomString2);

        validationHelper.validateCsrUpdate(uid, expectedContent, csrUpdate);
    }

    @Property(
            priority = Priority.BVT,
            features = "CSR Group",
            component = Component.CSR,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test get csr update list",
            steps = {
                    "1. Post csr update",
                    "2. Get csr update list ",
                    "3. verify response"

            }
    )
    @Test
    public void testGetCsrUpdates() throws Exception {
        String at = testDataProvider.postCsrAdminAccessToken();
        Master.getInstance().setCurrentUid(csrAdminUid);

        String uid = testDataProvider.getCsrAdminUid(at);

        String randomString = RandomFactory.getRandomStringOfAlphabet(100);
        testDataProvider.createCsrUpdate(uid, randomString);

        List<CsrUpdate> csrUpdateList = testDataProvider.getCsrUpdateList(true);

        boolean isFound = false;
        for (CsrUpdate csrUpdate : csrUpdateList) {
            if (csrUpdate.getContent().equals(randomString)) {
                isFound = true;
                break;
            }
        }

        if (!isFound) {
            throw new TestException("fail to find csr update whose content is :" + randomString);
        }
    }

    @Property(
            priority = Priority.BVT,
            features = "CSR Group",
            component = Component.CSR,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test delete csr update",
            steps = {
                    "1. Post csr update",
                    "2. Delete it",
                    "3. Get csr update",
                    "2. verify response"

            }
    )
    @Test
    public void testDeleteCsrUpdate() throws Exception {
        String at = testDataProvider.postCsrAdminAccessToken();
        Master.getInstance().setCurrentUid(csrAdminUid);

        String uid = testDataProvider.getCsrAdminUid(at);

        String randomString = RandomFactory.getRandomStringOfAlphabet(100);
        CsrUpdate csrUpdate = testDataProvider.createCsrUpdate(uid, randomString);

        testDataProvider.deleteCsrUpdate(IdConverter.idToHexString(csrUpdate.getId()));

        testDataProvider.getCsrUpdate(IdConverter.idToHexString(csrUpdate.getId()), 412);

        assert Master.getInstance().getApiErrorMsg().contains("CSR Update Not Found");

    }

    @Property(
            priority = Priority.BVT,
            features = "CSR Group",
            component = Component.CSR,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test put csr update",
            steps = {
                    "1. Post csr update",
                    "2. Update csr update",
                    "3. Get csr update",
                    "4. verify response"

            }
    )
    @Test
    public void testPutCsrUpdate() throws Exception {
        String at = testDataProvider.postCsrAdminAccessToken();
        Master.getInstance().setCurrentUid(csrAdminUid);

        String uid = testDataProvider.getCsrAdminUid(at);

        String randomString = RandomFactory.getRandomStringOfAlphabet(100);
        CsrUpdate csrUpdate = testDataProvider.createCsrUpdate(uid, RandomFactory.getRandomStringOfAlphabet(100));

        csrUpdate.setContent(randomString);
        testDataProvider.putCsrUpdate(IdConverter.idToHexString(csrUpdate.getId()), csrUpdate);

        CsrUpdate update = testDataProvider.getCsrUpdate(IdConverter.idToHexString(csrUpdate.getId()));

        validationHelper.validateCsrUpdate(uid, randomString, update);
    }

}
