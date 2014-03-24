/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.testing.common.exception.TestException;
import com.junbo.testing.common.libs.LogHelper;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.providers.netty.NettyResponse;
import junit.framework.Assert;

import java.util.HashMap;
import java.util.concurrent.Future;

/**
 * Created by Yunlong on 3/20/14.
 */
public abstract class HttpClientBase {
    private LogHelper logger = new LogHelper(HttpClientBase.class);
    private AsyncHttpClient asyncClient = new AsyncHttpClient();

    public static String contentType = "application/json";

    /**
     * Enum for http method.
     *
     * @author Yunlongzhao
     */
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
        headers.add(Header.CONTENT_TYPE, contentType);

        //for further header, we can set dynamic value from properties here
        return headers;
    }

    protected <T> T restApiCall(HTTPMethod httpMethod, String restUrl, T t) throws Exception {
        String requestBody = new JsonMessageTranscoder().encode(t);

        return restApiCall(httpMethod, restUrl, requestBody);
    }

    protected <T> T restApiCall(HTTPMethod httpMethod, String restUrl, T t,
                                int expectedResponseCode) throws Exception {
        String requestBody = new JsonMessageTranscoder().encode(t);

        return restApiCall(httpMethod, restUrl, requestBody, expectedResponseCode);
    }

    protected <T> T restApiCall(HTTPMethod httpMethod, String restUrl) throws Exception {
        return restApiCall(httpMethod, restUrl, null);
    }

    protected <T> T restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody) throws Exception {
        return restApiCall(httpMethod, restUrl, requestBody, 200);
    }

    protected <T> T restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody,
                                int expectedResponseCode) throws Exception {
        return restApiCall(httpMethod, restUrl, requestBody, expectedResponseCode, null);
    }

    protected <T> T restApiCall(HTTPMethod httpMethod, String restUrl, int expectedResponseCode) throws Exception {
        return restApiCall(httpMethod, restUrl, null, expectedResponseCode);
    }

    protected <T> T restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody,
                                int expectedResponseCode, HashMap<String, String> httpParameters) throws Exception {
        switch (httpMethod) {
            case PUT:
            case POST: {
                Request req = new RequestBuilder(httpMethod.getHttpMethod())
                        .setUrl(restUrl)
                        .setHeaders(getHeader())
                        .setBody(requestBody)
                        .build();

                logger.LogRequest(req);

                Future future = asyncClient.prepareRequest(req).execute();
                NettyResponse nettyResponse = (NettyResponse) future.get();

                logger.logInfo(String.format("http response code: %s", nettyResponse.getStatusCode()));
                if (expectedResponseCode != 0) {
                    Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());
                }

                logger.LogResponse(nettyResponse);

                return (T) new JsonMessageTranscoder().decode(
                        new TypeReference<Results<T>>() {
                        }, nettyResponse.getResponseBody());
            }
            case GET: {
                //append URL paras for http get method
                if (httpParameters != null && !httpParameters.isEmpty()) {
                    restUrl.concat("?");
                    for (String key : httpParameters.keySet()) {
                        restUrl.concat(String.format("&%s=%s", key, httpParameters.get(key)));
                    }
                }

                Request req = new RequestBuilder("GET")
                        .setUrl(restUrl)
                        .setHeaders(getHeader())
                        .build();


                logger.LogRequest(req);

                Future future = asyncClient.prepareRequest(req).execute();
                NettyResponse nettyResponse = (NettyResponse) future.get();
                //handle redirect url logic for getPrimaryCart etc.
                if (nettyResponse.getStatusCode() == 302) {
                    logger.logInfo(String.format("http response code: %s", nettyResponse.getStatusCode()));

                    String redirectUrl = nettyResponse.getHeaders().get("Location").get(0);
                    req = new RequestBuilder("GET")
                            .setUrl(redirectUrl)
                            .setHeaders(getHeader())
                            .build();

                    logger.LogRequest(req);

                    future = asyncClient.prepareRequest(req).execute();
                    nettyResponse = (NettyResponse) future.get();
                }

                logger.logInfo(String.format("http response code: %s", nettyResponse.getStatusCode()));

                if (expectedResponseCode != 0) {
                    Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());
                }

                logger.LogResponse(nettyResponse);

                return (T) new JsonMessageTranscoder().decode(
                        new TypeReference<Results<T>>() {
                        }, nettyResponse.getResponseBody());
            }
            case DELETE:
                //TODO
            case OPTIONS:
                //TODO
            default:
                throw new TestException(String.format("Unsupported http method found: %s", httpMethod.getHttpMethod()));

        }
    }

}
