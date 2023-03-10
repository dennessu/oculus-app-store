/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper;

// CHECKSTYLE:OFF

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RandomFactory;
import com.ning.http.client.*;
import com.ning.http.client.cookie.Cookie;
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig;
import com.ning.http.client.providers.netty.NettyResponse;
import org.apache.http.client.HttpResponseException;
import org.testng.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Yunlong on 3/20/14.
 */
public abstract class HttpClientBase {
    protected LogHelper logger = new LogHelper(HttpClientBase.class);
    protected AsyncHttpClient asyncClient;

    public String contentType = "application/json";

    protected ComponentType componentType;

    protected Master.EndPointType currentEndPointType;

    protected String endPointUrlSuffix = "";

    protected String uid = "";


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

    public HttpClientBase() {
        asyncClient = getAsyncHttpClient();
    }

    protected AsyncHttpClient getAsyncHttpClient() {

        AsyncHttpClientConfigBean config = new AsyncHttpClientConfigBean();
        NettyAsyncHttpProviderConfig nettyConfig = new NettyAsyncHttpProviderConfig();
        int maxHeadersSize = 65536;
        nettyConfig.addProperty(NettyAsyncHttpProviderConfig.HTTP_CLIENT_CODEC_MAX_HEADER_SIZE, maxHeadersSize);
        nettyConfig.addProperty(NettyAsyncHttpProviderConfig.HTTPS_CLIENT_CODEC_MAX_HEADER_SIZE, maxHeadersSize);
        config.setProviderConfig(nettyConfig);
        config.setMaxRequestRetry(3);

        return new AsyncHttpClient(config);
    }

    protected FluentCaseInsensitiveStringsMap getHeader(boolean isServiceScope, List<String> headersToRemove) {
        FluentCaseInsensitiveStringsMap headers = new FluentCaseInsensitiveStringsMap();
        headers.add(Header.CONTENT_TYPE, contentType);
        headers.add(Header.OCULUS_INTERNAL, String.valueOf(true));
        String uid = Master.getInstance().getCurrentUid();
        if (ConfigHelper.getSetting("testClientEncrypted") != null &&
                ConfigHelper.getSetting("testClientEncrypted").equals(String.valueOf(true))) {
            headers.add(Header.X_ENABLE_PROFILING, "10");
        }
        if (isServiceScope) {
            headers.add(Header.AUTHORIZATION, "Bearer " + Master.getInstance().getServiceAccessToken(componentType));
        } else if (uid != null && Master.getInstance().getUserAccessToken(uid) != null) {
            headers.add(Header.AUTHORIZATION, "Bearer " + Master.getInstance().getUserAccessToken(uid));
        }

        if (currentEndPointType != null && currentEndPointType.equals(Master.EndPointType.Secondary)) {
            headers.put("Cache-Control", Collections.singletonList("no-cache"));
        }

        if (headersToRemove != null && !headersToRemove.isEmpty()) {
            for (String headerToRemove : headersToRemove) {
                headers.remove(headerToRemove);
            }
        }

        //for further header, we can set dynamic value from properties here
        headers.add(Header.USER_IP, RandomFactory.getRandomIp());
        return headers;
    }

    protected <T> String restApiCall(HTTPMethod httpMethod, String restUrl, T t) throws Exception {
        byte[] bytes = new JsonMessageTranscoder().encode(t);
        String requestBody = new String(bytes);

        return restApiCall(httpMethod, restUrl, requestBody, false);
    }

    protected <T> String restApiCall(HTTPMethod httpMethod, String restUrl, boolean isServiceScope) throws Exception {
        return restApiCall(httpMethod, restUrl, null, isServiceScope);
    }

    protected <T> String restApiCall(HTTPMethod httpMethod, String restUrl, T t, boolean isServiceScope)
            throws Exception {
        byte[] bytes = new JsonMessageTranscoder().encode(t);
        String requestBody = new String(bytes);

        return restApiCall(httpMethod, restUrl, requestBody, isServiceScope);
    }

    protected <T> String restApiCall(HTTPMethod httpMethod, String restUrl, T t,
                                     int expectedResponseCode) throws Exception {
        return restApiCall(httpMethod, restUrl, t, expectedResponseCode, false);
    }

    protected <T> String restApiCall(HTTPMethod httpMethod, String restUrl, T t,
                                     int expectedResponseCode, boolean isServiceScope) throws Exception {
        byte[] bytes = new JsonMessageTranscoder().encode(t);
        String requestBody = new String(bytes);

        return restApiCall(httpMethod, restUrl, requestBody, expectedResponseCode, isServiceScope);
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl) throws Exception {
        return restApiCall(httpMethod, restUrl, null);
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody) throws Exception {
        return restApiCall(httpMethod, restUrl, requestBody, 200, false);
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody,
                                 boolean isServiceScope) throws Exception {
        return restApiCall(httpMethod, restUrl, requestBody, 200, isServiceScope);
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody,
                                 int expectedResponseCode) throws Exception {
        return restApiCall(httpMethod, restUrl, requestBody, expectedResponseCode, null, false);
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody,
                                 int expectedResponseCode, boolean isServiceScope) throws Exception {
        return restApiCall(httpMethod, restUrl, requestBody, expectedResponseCode, null, isServiceScope);
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody, int expectedResponseCode,
                                 boolean isServiceScope, List<String> headersToRemove) throws Exception {
        return restApiCall(httpMethod, restUrl, requestBody, expectedResponseCode, null, isServiceScope, headersToRemove);
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl, int expectedResponseCode) throws Exception {
        return restApiCall(httpMethod, restUrl, null, expectedResponseCode, null);
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody,
                                 int expectedResponseCode, HashMap<String, List<String>> httpParameters,
                                 boolean isServiceScope) throws Exception {
        return restApiCall(httpMethod, restUrl, requestBody, expectedResponseCode, httpParameters, isServiceScope, null);
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody,
                                 int expectedResponseCode, HashMap<String, List<String>> httpParameters,
                                 boolean isServiceScope, List<String> headersToRemove) throws Exception {
        switch (httpMethod) {
            case PUT:
            case POST: {
                try {
                    Request req;
                    if (Master.getInstance().getCookies().size() > 0) {
                        req = new RequestBuilder(httpMethod.getHttpMethod())
                                .setUrl(restUrl)
                                .setHeaders(getHeader(isServiceScope, headersToRemove))
                                .setBodyEncoding("UTF-8")
                                .setBody(requestBody)
                                .addCookie(Master.getInstance().getCookies().get(0))
                                .build();
                    } else {
                        req = new RequestBuilder(httpMethod.getHttpMethod())
                                .setUrl(restUrl)
                                .setHeaders(getHeader(isServiceScope, headersToRemove))
                                .setBodyEncoding("UTF-8")
                                .setBody(requestBody)
                                .build();
                    }

                    logger.LogRequest(req);

                    Future future = asyncClient.prepareRequest(req).execute();
                    NettyResponse nettyResponse = (NettyResponse) future.get();

                    logger.LogResponse(nettyResponse);
                    if (expectedResponseCode != 0) {
                        Assert.assertEquals(nettyResponse.getStatusCode(), expectedResponseCode);
                    }

                    if (expectedResponseCode != 200) {
                        Master.getInstance().setApiErrorMsg(nettyResponse.getResponseBody());
                    }
                    return nettyResponse.getResponseBody("UTF-8");
                } catch (HttpResponseException ex) {
                    throw new TestException(ex.getMessage().toString());
                }
            }
            case GET: {
                try {
                    if (httpParameters != null && !httpParameters.isEmpty()) {
                        restUrl = restUrl.concat("?");
                        for (String key : httpParameters.keySet()) {
                            List<String> strValues = httpParameters.get(key);
                            for (int i = 0; i < strValues.size(); i++) {
                                restUrl = restUrl.concat(String.format("%s=%s", key, strValues.get(i)));
                                restUrl = restUrl.concat("&");
                            }
                        }
                        //Remove the last "&" character
                        restUrl = restUrl.substring(0, restUrl.length() - 1);
                    }
                    Request req;
                    if (Master.getInstance().getCookies().size() > 0) {
                        req = new RequestBuilder("GET")
                                .setUrl(restUrl)
                                .addCookie(Master.getInstance().getCookies().get(0))
                                .setHeaders(getHeader(isServiceScope, headersToRemove))
                                .build();
                    } else {
                        req = new RequestBuilder("GET")
                                .setUrl(restUrl)
                                .setHeaders(getHeader(isServiceScope, headersToRemove))
                                .build();
                    }

                    logger.LogRequest(req);

                    Future future = asyncClient.prepareRequest(req).execute();
                    NettyResponse nettyResponse = (NettyResponse) future.get();
                    //handle redirect url logic for getPrimaryCart etc.
                    if (nettyResponse.getStatusCode() == 302) {
                        logger.logInfo(String.format("http response code: %s", nettyResponse.getStatusCode()));
                        Thread.sleep(3000);
                        String redirectUrl = nettyResponse.getHeaders().get("Location").get(0);
                        if (redirectUrl.contains("cid")) {
                            List<Cookie> cookies = nettyResponse.getCookies();
                            if (cookies != null && cookies.size() > 0) {
                                Master.getInstance().addCookie(cookies.get(0));
                            }
                            return redirectUrl;
                        }

                        if (redirectUrl.contains("email-verify")) {
                            return redirectUrl;
                        }
                        req = new RequestBuilder("GET")
                                .setUrl(redirectUrl)
                                .setHeaders(getHeader(isServiceScope, headersToRemove))
                                .build();

                        logger.LogRequest(req);

                        future = asyncClient.prepareRequest(req).execute();
                        nettyResponse = (NettyResponse) future.get();
                        expectedResponseCode = nettyResponse.getStatusCode();
                    }

                    logger.LogResponse(nettyResponse);
                    if (expectedResponseCode != 0) {
                        Assert.assertEquals(nettyResponse.getStatusCode(), expectedResponseCode);
                    }

                    if (expectedResponseCode != 200) {
                        Master.getInstance().setApiErrorMsg(nettyResponse.getResponseBody());
                    }

                    return nettyResponse.getResponseBody("UTF-8");
                } catch (HttpResponseException ex) {
                    throw new TestException(ex.getMessage().toString());
                }

            }
            case DELETE: {
                try {
                    Request req = new RequestBuilder(httpMethod.getHttpMethod())
                            .setUrl(restUrl)
                            .setHeaders(getHeader(isServiceScope, headersToRemove))
                            .build();

                    logger.LogRequest(req);

                    Future future = asyncClient.prepareRequest(req).execute();
                    NettyResponse nettyResponse = (NettyResponse) future.get();

                    logger.LogResponse(nettyResponse);
                    if (expectedResponseCode != 0) {
                        Assert.assertEquals(nettyResponse.getStatusCode(), expectedResponseCode);
                    }

                    if (expectedResponseCode != 200) {
                        Master.getInstance().setApiErrorMsg(nettyResponse.getResponseBody());
                    }

                    return nettyResponse.getResponseBody("UTF-8");
                } catch (HttpResponseException ex) {
                    throw new TestException(ex.getMessage().toString());
                }
            }
            case OPTIONS:
                //TODO
            default:
                throw new TestException(String.format("Unsupported http method found: %s", httpMethod.getHttpMethod()));
        }
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody,
                                 int expectedResponseCode, HashMap<String, List<String>> httpParameters)
            throws Exception {
        return restApiCall(httpMethod, restUrl, requestBody, expectedResponseCode, httpParameters, false);
    }

    protected String readFileContent(String resourceLocation) throws Exception {

        InputStream inStream = ClassLoader.getSystemResourceAsStream(resourceLocation);
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));

        StringBuilder strItem = new StringBuilder();
        try {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                strItem.append(sCurrentLine + "\n");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null) {
                br.close();
            }
            if (inStream != null) {
                inStream.close();
            }
        }

        return strItem.toString();
    }

    protected boolean isServiceTokenExist(ComponentType componentType) {
        return Master.getInstance().getServiceAccessToken(componentType) != null;
    }

    protected boolean isServiceTokenExist() {
        return Master.getInstance().getServiceAccessToken(componentType) != null;
    }

    protected String getEndPointUrl() {
        switch (Master.getInstance().getEndPointType()) {
            case Primary:
                currentEndPointType = Master.EndPointType.Primary;
                return Master.getInstance().getPrimaryCommerceEndPointUrl() + endPointUrlSuffix;
            case Secondary:
                currentEndPointType = Master.EndPointType.Secondary;
                return Master.getInstance().getSecondaryCommerceEndPointUrl() + endPointUrlSuffix;
            default:
                throw new TestException("No such endpoint type");
        }
    }
}
