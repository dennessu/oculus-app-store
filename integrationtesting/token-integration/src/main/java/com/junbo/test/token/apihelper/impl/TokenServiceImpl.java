/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.token.apihelper.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.token.apihelper.TokenService;
import com.junbo.token.spec.model.TokenConsumption;
import com.junbo.token.spec.model.TokenItem;
import com.junbo.token.spec.model.TokenRequest;

/**
 * Created by weiyu_000 on 7/24/14.
 */
public class TokenServiceImpl extends HttpClientBase implements TokenService {

    private static String tokenUrl = ConfigHelper.getSetting("defaultCommerceEndpoint") + "/tokens/";
    private OAuthService oAuthTokenClient = OAuthServiceImpl.getInstance();
    private static TokenService instance;

    public static synchronized TokenService getInstance() {
        if (instance == null) {
            instance = new TokenServiceImpl();
        }
        return instance;
    }

    private TokenServiceImpl() {
        componentType = ComponentType.TOKEN;
    }

    @Override
    public TokenRequest postTokenRequest(TokenRequest tokenRequest) throws Exception {
        return postTokenRequest(tokenRequest, 200);
    }

    @Override
    public TokenRequest postTokenRequest(TokenRequest tokenRequest, int expectedResponseCode) throws Exception {
        if (!isServiceTokenExist()) {
            oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, componentType);
        }
        String responseBody = restApiCall(HTTPMethod.POST, tokenUrl + "/requests", tokenRequest,
                expectedResponseCode, true);

        if (expectedResponseCode == 200) {
            return new JsonMessageTranscoder().decode(new TypeReference<TokenRequest>() {
            }, responseBody);
        }
        return null;

    }

    @Override
    public TokenRequest getTokenByTokenId(String tokenId) throws Exception {
        return getTokenByTokenId(tokenId, 200);
    }

    @Override
    public TokenRequest getTokenByTokenId(String tokenId, int expectedResponseCode) throws Exception {
        if (!isServiceTokenExist()) {
            oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, componentType);
        }
        String responseBody = restApiCall(HTTPMethod.GET, tokenUrl + "/requests/" + tokenId, expectedResponseCode, true);
        if (expectedResponseCode == 200) {
            return new JsonMessageTranscoder().decode(new TypeReference<TokenRequest>() {
            }, responseBody);
        }
        return null;
    }

    @Override
    public TokenConsumption postTokenConsumption(TokenConsumption tokenConsumption) throws Exception {
        return postTokenConsumption(tokenConsumption, 200);
    }

    @Override
    public TokenConsumption postTokenConsumption(TokenConsumption tokenConsumption, int expectedResponseCode)
            throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, tokenUrl + "/consumption", tokenConsumption, 200, false);

        TokenConsumption tokenConsumptionResult = new JsonMessageTranscoder().decode(
                new TypeReference<TokenConsumption>() {
                }, responseBody);

        if (expectedResponseCode == 200) {
            return tokenConsumptionResult;
        } else {
            return null;
        }
    }

    @Override
    public TokenItem updateTokenItem(String tokenId, TokenItem tokenItem) throws Exception {
        return updateTokenItem(tokenId, tokenItem, 200);
    }

    @Override
    public TokenItem updateTokenItem(String tokenId, TokenItem tokenItem, int expectedResponseCode) throws Exception {
        if (!isServiceTokenExist()) {
            oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, componentType);
        }
        String responseBody = restApiCall(HTTPMethod.PUT, tokenUrl + "/"+ tokenId,
                tokenItem, expectedResponseCode, true);

        TokenItem tokenItemResult = new JsonMessageTranscoder().decode(
                new TypeReference<TokenItem>() {
                }, responseBody);

        if (expectedResponseCode == 200) {
            return tokenItemResult;
        } else {
            return null;
        }
    }

    @Override
    public TokenItem getTokenItem(String tokenItemId) throws Exception {
        return getTokenItem(tokenItemId, 200);
    }

    @Override
    public TokenItem getTokenItem(String tokenItemId, int expectedResponseCode) throws Exception {
        if (!isServiceTokenExist()) {
            oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, componentType);
        }
        String responseBody = restApiCall(HTTPMethod.GET, tokenUrl + "/"+ tokenItemId, expectedResponseCode, true);

        TokenItem tokenItem = new JsonMessageTranscoder().decode(
                new TypeReference<TokenItem>() {
                }, responseBody);

        if (expectedResponseCode == 200) {
            return tokenItem;
        } else {
            return null;
        }

    }

}
