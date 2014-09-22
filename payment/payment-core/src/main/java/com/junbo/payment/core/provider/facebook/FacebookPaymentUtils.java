/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.facebook;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.facebook.FacebookOauthApi;
import com.junbo.payment.clientproxy.facebook.FacebookTokenRequest;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppServerExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facebook Payment Utils.
 */
public class FacebookPaymentUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FacebookPaymentUtils.class);
    private String token;
    private FacebookOauthApi facebookPaymentApi;
    private String oculusAppId;
    private String oculusAppSecret;

    public Promise<String> getAccessToken(){
        if(!CommonUtil.isNullOrEmpty(token)){
            return Promise.pure(token);
        }
        FacebookTokenRequest tokenRequest = new FacebookTokenRequest();
        tokenRequest.setClientId(oculusAppId);
        tokenRequest.setClientSecret(oculusAppSecret);
        return facebookPaymentApi.getAccessToken(tokenRequest).then(new Promise.Func<String, Promise<String>>() {
            @Override
            public Promise<String> apply(String s) {
                if(CommonUtil.isNullOrEmpty(s)){
                    LOGGER.error("unable to get the access token for facebook graph API");
                    throw AppServerExceptions.INSTANCE.unAuthorized("facebook graph API").exception();
                }
                token = s.replace("access_token=", "");
                return Promise.pure(token);
            }
        });
    }

    public String getToken() {
        return token;
    }

    public void setOculusAppSecret(String oculusAppSecret) {
        this.oculusAppSecret = oculusAppSecret;
    }

    public void setFacebookPaymentApi(FacebookOauthApi facebookPaymentApi) {
        this.facebookPaymentApi = facebookPaymentApi;
    }

    public void setOculusAppId(String oculusAppId) {
        this.oculusAppId = oculusAppId;
    }
}
