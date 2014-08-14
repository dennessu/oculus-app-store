/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.commerce.apiHelper.impl.utility;

import com.junbo.oauth.spec.model.TokenInfo;
import com.junbo.test.commerce.apiHelper.OauthService;
import com.junbo.test.commerce.apiHelper.impl.OauthServiceImpl;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;

/**
 * Created by weiyu_000 on 8/14/14.
 */
public class CommerceTestDataHelper extends BaseTestDataProvider {
    private OauthService oauthClient = OauthServiceImpl.instance();
    protected UserService identityClient = UserServiceImpl.instance();

    private String getCid() throws Exception {
        return oauthClient.getCid();
    }

    public String createUser() throws Exception {
        String cid = getCid();
        oauthClient.authorizeLoginView(cid);
        oauthClient.authorizeRegister(cid);
        String userName = RandomFactory.getRandomStringOfAlphabet(5);
        oauthClient.registerUser(userName, "Test1234", Country.DEFAULT, cid);
        String accessToken = oauthClient.postUserAccessToken(userName, "Test1234");
        TokenInfo tokenInfo = oauthClient.getTokenInfo(accessToken);
        String uid = IdConverter.idToHexString(tokenInfo.getSub());
        Master.getInstance().addUserAccessToken(uid, accessToken);
        Master.getInstance().setCurrentUid(uid);
        identityClient.UpdateUserPersonalInfo(uid ,userName, "Test1234");

        return uid ;
    }


}
