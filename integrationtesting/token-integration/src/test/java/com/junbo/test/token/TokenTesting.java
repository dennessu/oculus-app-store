/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.token;

import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.token.spec.enums.ItemStatus;
import com.junbo.token.spec.model.TokenItem;
import com.junbo.token.spec.model.TokenRequest;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Created by weiyu_000 on 7/24/14.
 */
public class TokenTesting extends BaseTokenTestClass {

    @Property(
            priority = Priority.BVT,
            features = "Post /tokens/requests",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test Post token request",
            steps = {
                    "1. Post token request",
                    "2. Verify token response"
            }
    )
    @Test
    public void testPostTokenRequest() throws Exception {
        TokenRequest tokenRequestResult = testDataProvider.PostTokenRequest(offer_digital_normal1);
        //TODO verify token response
    }

    @Property(
            priority = Priority.BVT,
            features = "Get /tokens/requests/{key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test get token request",
            steps = {
                    "1. Post token request",
                    "2. Get token request by response Id",
                    "3. Verify token response"
            }
    )
    @Test
    public void testGetTokenRequestByTokenId() throws Exception {
        TokenRequest tokenRequestResult = testDataProvider.PostTokenRequest(offer_digital_normal1);
        testDataProvider.GetTokenRequest(tokenRequestResult.getId());

        //TODO verify token response
    }


    @Property(
            priority = Priority.BVT,
            features = "Post /tokens/consumption",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test post token consumption",
            steps = {
                    "1. Post token consumption",
                    "2. Verify token response"
            }
    )
    @Test
    public void testPostTokenConsumption() throws Exception {
        String uid = testDataProvider.CreateUser();
        TokenRequest tokenRequestResult = testDataProvider.PostTokenRequest(offer_digital_normal1);
        testDataProvider.postTokenConsumption(uid, offer_digital_normal1,
                tokenRequestResult.getTokenItems().get(0).getEncryptedString());

        //TODO verify token response
    }

    @Property(
            priority = Priority.BVT,
            features = "Get /tokens/{key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test get token item",
            steps = {
                    "1. Post token request",
                    "2. Get token item by id",
                    "3  Verify token item response",
            }
    )
    @Test
    public void testGetTokenItem() throws Exception {
        TokenRequest tokenRequestResult = testDataProvider.PostTokenRequest(offer_digital_normal1);

        testDataProvider.getTokenItem(tokenRequestResult.getTokenItems().get(0).getEncryptedString());

        //TODO verify token response
    }

    @Property(
            priority = Priority.BVT,
            features = "Get /tokens/{key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test get token item",
            steps = {
                    "1. Post token request",
                    "2. Get token item by id",
                    "3  Verify token item response",
            }
    )
    @Test
    public void testUpdateTokenItem() throws Exception {
        TokenRequest tokenRequestResult = testDataProvider.PostTokenRequest(offer_digital_normal1);

        TokenItem tokenItem = testDataProvider.getTokenItem(tokenRequestResult.getTokenItems()
                .get(0).getEncryptedString());

        tokenItem.setStatus(ItemStatus.BLACKLISTED.toString());
        tokenItem.setDisableReason("ut");

        testDataProvider.updateTokenItem(tokenItem);

        //TODO verify token response
    }


}
