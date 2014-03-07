/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.provider.impl;

import com.junbo.email.common.exception.AppExceptions;
import com.junbo.email.core.provider.EmailProvider;
import com.junbo.email.core.provider.Request;
import com.junbo.email.core.provider.Response;
import com.junbo.email.core.provider.model.mandrill.MandrillResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

/**
 * Impl of EmailProvider.
 */
@Component
public class EmailProviderImpl implements EmailProvider {

    private CloseableHttpClient client;

    public EmailProviderImpl() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        client= HttpClients.custom().setConnectionManager(cm).build();
    }
    public Response send(Request request) {
        Response response = new MandrillResponse();
        HttpPost httpPost = new HttpPost(request.getUri());
        StringEntity stringEntity = new StringEntity(request.getJson(),ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);
        try {
            CloseableHttpResponse httpResponse = client.execute(httpPost);
            HttpEntity responseEntity = httpResponse.getEntity();
            int statusCode  = httpResponse.getStatusLine().getStatusCode();
            if(responseEntity != null) {
                String str = EntityUtils.toString(responseEntity);
                response = new MandrillResponse();
                response.setBody(str);
                response.setStatusCode(statusCode);
            }
        } catch (Exception e) {
            throw AppExceptions.INSTANCE.internalError().exception();
        }
        finally {
            httpPost.releaseConnection();
        }
        return response;
    }
}
