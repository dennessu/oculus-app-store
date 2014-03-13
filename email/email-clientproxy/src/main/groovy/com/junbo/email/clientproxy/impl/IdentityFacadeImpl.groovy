/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.clientproxy.impl

import com.junbo.common.id.UserId
import com.junbo.common.json.IdPathParamTranscoder
import com.junbo.common.json.JsonMessageTranscoder
import com.junbo.common.json.QueryParamTranscoderImpl
import com.junbo.email.clientproxy.IdentityFacade
import com.junbo.identity.spec.model.user.User
import com.junbo.identity.spec.resource.proxy.UserResourceClientProxy
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncHttpClientConfigBean
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Impl of IdentityFacade.
 */
@CompileStatic
class IdentityFacadeImpl implements IdentityFacade {

    @Autowired
    final AsyncHttpClient asyncHttpClient

    String url

    void setUrl(String url) {
        this.url = url
    }

    IdentityFacadeImpl() {
        if (asyncHttpClient == null) {
            asyncHttpClient = new AsyncHttpClient(new AsyncHttpClientConfigBean())
        }
    }

    Promise<User> getUser(Long userId) {
        new UserResourceClientProxy(asyncHttpClient, new JsonMessageTranscoder(),
                new IdPathParamTranscoder(), new QueryParamTranscoderImpl(), url).getUser(new UserId(userId))
    }
}
