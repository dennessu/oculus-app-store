/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.token.utility;

import com.junbo.common.id.UserId;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.crypto.apihelper.CryptoService;
import com.junbo.test.crypto.apihelper.impl.CryptoServiceImpl;
import com.junbo.test.token.apihelper.TokenService;
import com.junbo.test.token.apihelper.impl.TokenServiceImpl;
import com.junbo.token.spec.model.ProductDetail;
import com.junbo.token.spec.model.TokenConsumption;
import com.junbo.token.spec.model.TokenItem;
import com.junbo.token.spec.model.TokenRequest;

/**
 * Created by weiyu_000 on 7/24/14.
 */
public class TokenTestDataProvider extends BaseTestDataProvider {
    private TokenService tokenClient = TokenServiceImpl.getInstance();
    private OfferService offerClient = OfferServiceImpl.instance();
    private CryptoService cryptoClient = CryptoServiceImpl.getInstance();
    private UserService identityClient = UserServiceImpl.instance();

    public String CreateUser() throws Exception {
        return identityClient.PostUser();
    }

    public TokenRequest PostTokenRquest(String offerName) throws Exception {
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setActivation("yes");
        tokenRequest.setCreateMethod("GENERATION");
        tokenRequest.setDescription("ut");
        tokenRequest.setGenerationLength("LEN16");
        ProductDetail productDetail = new ProductDetail();
        productDetail.setDefaultOffer(offerClient.getOfferIdByName(offerName));
        tokenRequest.setProductDetail(productDetail);
        tokenRequest.setProductType("OFFER");
        tokenRequest.setQuantity(1L);
        tokenRequest.setUsageLimit("1");
        return tokenClient.postTokenRequest(tokenRequest);
    }

    public TokenRequest GetTokenRequest(String tokenRequestId) throws Exception {
        return tokenClient.getTokenByTokenId(tokenRequestId);
    }

    public TokenConsumption postTokenConsumption(String uid, String offerName, String tokenStr) throws Exception {
        TokenConsumption tokenConsumption = new TokenConsumption();
        tokenConsumption.setTokenString(cryptoClient.decryptCryptoMessage(tokenStr));
        tokenConsumption.setProduct(offerClient.getOfferIdByName(offerName));
        tokenConsumption.setUserId(IdConverter.hexStringToId(UserId.class, uid));
        return tokenClient.postTokenConsumption(tokenConsumption);

    }

    public TokenItem getTokenItem(String tokenString) throws Exception {
        String tokenStr = cryptoClient.decryptCryptoMessage(tokenString);
        return tokenClient.getTokenItem(tokenStr);
    }

    public TokenItem updateTokenItem(TokenItem tokenItem) throws Exception {
        return tokenClient.updateTokenItem(tokenItem.getEncryptedString(), tokenItem);
    }

}
