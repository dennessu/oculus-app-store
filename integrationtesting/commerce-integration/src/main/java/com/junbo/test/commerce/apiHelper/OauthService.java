/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.commerce.apiHelper;

import com.junbo.oauth.spec.model.TokenInfo;
import com.junbo.test.common.Entities.enums.Country;

/**
 * Created by weiyu_000 on 8/14/14.
 */
public interface OauthService {
    //filter out cid from respond url
    String getCid() throws Exception;

    void authorizeLoginView(String cid) throws Exception;

    void authorizeRegister(String cid) throws Exception;

    void registerUser(String userName, String password, Country country, String cid) throws Exception;

    String postUserAccessToken(String username, String pwd) throws Exception;

    TokenInfo getTokenInfo(String accessToken) throws Exception;

}
