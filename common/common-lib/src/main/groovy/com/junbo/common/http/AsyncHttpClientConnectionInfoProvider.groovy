/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.http

import com.junbo.apphost.core.health.ConnectionInfoProvider
import com.junbo.langur.core.async.JunboAsyncHttpClient
import com.ning.http.client.AsyncHttpClient
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * AsyncHttpClientConnectionInfoProvider.
 */
@CompileStatic
class AsyncHttpClientConnectionInfoProvider implements ConnectionInfoProvider {
    private JunboAsyncHttpClient junboAsyncHttpClient

    @Required
    void setJunboAsyncHttpClient(JunboAsyncHttpClient junboAsyncHttpClient) {
        this.junboAsyncHttpClient = junboAsyncHttpClient
    }

    @Override
    Map getConnectionInfo() {
        AsyncHttpClient client = junboAsyncHttpClient.getAsyncHttpClient()
        return ['asyncHttpClient' : client.getProvider().toString()] as Map<String, String>
    }
}
