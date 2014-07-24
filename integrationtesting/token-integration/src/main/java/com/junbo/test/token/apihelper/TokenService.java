/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.token.apihelper;

import com.junbo.token.spec.model.TokenConsumption;
import com.junbo.token.spec.model.TokenItem;
import com.junbo.token.spec.model.TokenRequest;

/**
 * Created by weiyu_000 on 7/24/14.
 */
public interface TokenService {

    TokenRequest postTokenRequest(TokenRequest tokenRequest) throws Exception;

    TokenRequest postTokenRequest(TokenRequest tokenRequest, int expectedResponseCode) throws Exception;

    TokenRequest getTokenByTokenId(String tokenId) throws Exception;

    TokenRequest getTokenByTokenId(String tokenId, int expectedResponseCode) throws Exception;

    TokenConsumption postTokenConsumption(TokenConsumption tokenConsumption) throws Exception;

    TokenConsumption postTokenConsumption(TokenConsumption tokenConsumption, int expectedResponseCode) throws Exception;

    TokenItem updateTokenItem(TokenItem tokenItem) throws Exception;

    TokenItem updateTokenItem(TokenItem tokenItem, int expectedResponseCode) throws Exception;

    TokenItem getTokenItem(String tokenItemId) throws Exception;

    TokenItem getTokenItem(String tokenItemId, int expectedResponseCode) throws Exception;

}
