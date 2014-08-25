/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.crypto.apihelper.impl;

import com.junbo.crypto.spec.model.CryptoMessage;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.crypto.apihelper.CryptoService;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.test.common.ConfigHelper;

/**
 * Created by weiyu_000 on 7/28/14.
 */
public class CryptoServiceImpl extends HttpClientBase implements CryptoService {

    private static String cryptoUrl = ConfigHelper.getSetting("defaultCommerceEndpoint") + "crypto/";
    private static CryptoService instance;
    private OAuthService oAuthTokenClient = OAuthServiceImpl.getInstance();

    public static synchronized CryptoService getInstance() {
        if (instance == null) {
            instance = new CryptoServiceImpl();
        }
        return instance;
    }

    private CryptoServiceImpl() {
        componentType = ComponentType.CRYPTO;
    }


    @Override
    public String encryptCryptoMessage(String msg) throws Exception {
        return null;
    }

    @Override
    public String encryptCryptoMessage(String msg, int expectedResponseCode) throws Exception {
        return null;
    }

    @Override
    public String encryptCryptoMessage(String uid, String msg) throws Exception {
        return null;
    }

    @Override
    public String encryptCryptoMessage(String uid, String msg, int expectedResponseCode) throws Exception {
        return null;
    }

    @Override
    public String decryptCryptoMessage(String msg) throws Exception {
        return decryptCryptoMessage(msg, 200);
    }

    @Override
    public String decryptCryptoMessage(String msg, int expectedResponseCode) throws Exception {
        oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CRYPTO);
        CryptoMessage cryptoMessage = new CryptoMessage();
        cryptoMessage.setValue(msg);

        String responseBody = restApiCall(HTTPMethod.POST, cryptoUrl + "/decrypt", cryptoMessage,
                expectedResponseCode, true);

        cryptoMessage = new JsonMessageTranscoder().decode(
                new TypeReference<CryptoMessage>() {
                }, responseBody);

        if (expectedResponseCode == 200) {
            return cryptoMessage.getValue();
        } else {
            return null;
        }

    }

    @Override
    public String decryptCryptoMessage(String uid, String msg) throws Exception {
        return null;
    }

    @Override
    public String decryptoCryptoMessage(String uid, String msg, int expectedResponseCode) throws Exception {
        return null;
    }

}
