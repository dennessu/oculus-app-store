/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.core.client.impl

import com.junbo.cart.core.client.IdentityClient
import com.junbo.common.id.UserId
import com.junbo.common.json.JsonMessageTranscoder
import com.junbo.identity.spec.model.user.User
import com.junbo.identity.spec.resource.proxy.UserResourceClientProxy
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncHttpClientConfigBean

/**
 * Created by fzhang@wan-san.com on 14-2-18.
 */
class IdentityClientImpl implements IdentityClient {

    final private AsyncHttpClient asyncHttpClient

    private String identityUrl

    void setIdentityUrl(String identityUrl) {
        this.identityUrl = identityUrl
    }

    IdentityClientImpl() {
        if (asyncHttpClient == null) {
            asyncHttpClient = new AsyncHttpClient(new AsyncHttpClientConfigBean())
        }
    }

    @Override
    Promise<User> getUser(UserId userId) {
        return new UserResourceClientProxy(asyncHttpClient, new JsonMessageTranscoder(),
                identityUrl).getUser(userId.value)
    }
}
