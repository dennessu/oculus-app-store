/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.testing.common.libs.LogHelper;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.providers.netty.NettyResponse;

import java.util.concurrent.Future;

/**
 * Created by Yunlong on 3/20/14.
 */
public abstract class HttpClientBase {
    private LogHelper logger = new LogHelper(HttpClientBase.class);
    private AsyncHttpClient asyncClient;

    public static String CONTENT_TYPE = "application/json";

    public enum HTTPMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE"),
        OPTIONS("OPTIONS");

        private String methodName;

        private HTTPMethod(String methodName) {
            this.methodName = methodName;
        }

        public String getHttpMethod() {
            return methodName;
        }

    }

    protected FluentCaseInsensitiveStringsMap getHeader() {
        FluentCaseInsensitiveStringsMap headers = new FluentCaseInsensitiveStringsMap();
        headers.add(Header.CONTENT_TYPE, CONTENT_TYPE);
        //for further header, we can set dynamic value from properties here
        return headers;
    }


    protected <T> T restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody) throws Exception {
        switch (httpMethod) {
            case POST: {
                Request req = new RequestBuilder(httpMethod.getHttpMethod())
                        .setUrl(restUrl)
                        .setHeaders(getHeader())
                        .setBody(requestBody)
                        .build();

                logger.LogRequest(req);

                Future future = asyncClient.prepareRequest(req).execute();
                NettyResponse nettyResponse = (NettyResponse) future.get();

                logger.LogResponse(nettyResponse);

                return (T) new JsonMessageTranscoder().decode(
                        new TypeReference<ResultList<T>>() {
                        }, nettyResponse.getResponseBody());
            }
            case GET:
                Request req = new RequestBuilder("GET")
                        .setUrl(restUrl)
                        .setHeaders(getHeader())
                        .build();

                logger.LogRequest(req);
                break;

            case PUT:
                //TODO
            case DELETE:
                //TODO
            case OPTIONS:
                //TODO
            default:
                //TODO throw new Exception("");

        }

        return null;
    }

}
