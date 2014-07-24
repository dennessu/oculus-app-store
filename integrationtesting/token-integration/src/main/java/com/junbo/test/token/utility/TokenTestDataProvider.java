/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.token.utility;

import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.token.apihelper.TokenService;
import com.junbo.test.token.apihelper.impl.TokenServiceImpl;
import com.junbo.token.spec.model.ProductDetail;
import com.junbo.token.spec.model.TokenRequest;

/**
 * Created by weiyu_000 on 7/24/14.
 */
public class TokenTestDataProvider extends BaseTestDataProvider {
    private TokenService tokenClient = TokenServiceImpl.getInstance();
    protected OfferService offerClient = OfferServiceImpl.instance();

    public TokenRequest PostTokenRquest(String offerName) throws Exception{
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

    public TokenRequest GetTokenRequest(String tokenRequestId) throws Exception{
        return tokenClient.getTokenByTokenId(tokenRequestId);
    }

}
