/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper;

import com.junbo.test.common.blueprint.Master;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.ning.http.client.providers.netty.NettyResponse;
import com.junbo.test.common.exception.TestException;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.test.common.libs.LogHelper;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Request;

import java.util.concurrent.Future;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.testng.Assert;

import java.util.List;

/**
 * Created by Yunlong on 3/20/14.
 */
public abstract class HttpClientBase {
    private LogHelper logger = new LogHelper(HttpClientBase.class);
    private AsyncHttpClient asyncClient = new AsyncHttpClient();

    public static String contentType = "application/json";

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

    protected FluentCaseInsensitiveStringsMap getHeader(boolean isRoleAPI) {
        FluentCaseInsensitiveStringsMap headers = new FluentCaseInsensitiveStringsMap();
        headers.add(Header.CONTENT_TYPE, contentType);
        String uid = Master.getInstance().getCurrentUid();
        if (isRoleAPI) {
            headers.add(Header.AUTHORIZATION, "Bearer " + Master.getInstance().getIdentityAccessToken());
        } else if (uid != null && Master.getInstance().getUserAccessToken(uid) != null) {
            headers.add(Header.AUTHORIZATION, "Bearer " + Master.getInstance().getUserAccessToken(uid));
        } else {
            headers.add(Header.AUTHORIZATION, "Bearer " + Master.getInstance().getIdentityAccessToken());
        }

        //headers.add(Header.AUTHORIZATION, "Bearer " + Master.getInstance().getIdentityAccessToken());

        //for further header, we can set dynamic value from properties here
        return headers;
    }

    protected <T> String restApiCall(HTTPMethod httpMethod, String restUrl, T t) throws Exception {
        byte[] bytes = new JsonMessageTranscoder().encode(t);
        String requestBody = new String(bytes);

        return restApiCall(httpMethod, restUrl, requestBody);
    }

    protected <T> String restApiCall(HTTPMethod httpMethod, String restUrl, T t,
                                     int expectedResponseCode) throws Exception {
        byte[] bytes = new JsonMessageTranscoder().encode(t);
        String requestBody = new String(bytes);

        return restApiCall(httpMethod, restUrl, requestBody, expectedResponseCode);
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl) throws Exception {
        return restApiCall(httpMethod, restUrl, null);
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody) throws Exception {
        return restApiCall(httpMethod, restUrl, requestBody, 200);
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody,
                                 int expectedResponseCode) throws Exception {
        return restApiCall(httpMethod, restUrl, requestBody, expectedResponseCode, null);
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl, int expectedResponseCode) throws Exception {
        return restApiCall(httpMethod, restUrl, null, expectedResponseCode, null);
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody,
                                 int expectedResponseCode, HashMap<String, List<String>> httpParameters) throws Exception {
        boolean isRoleAPI = false;

        if (restUrl.contains("/v1/roles") || restUrl.contains("/v1/role-assignments")) {
            isRoleAPI = true;
        }

        switch (httpMethod) {
            case PUT:
            case POST: {
                Request req = new RequestBuilder(httpMethod.getHttpMethod())
                        .setUrl(restUrl)
                        .setHeaders(getHeader(isRoleAPI))
                        .setBody(requestBody)
                        .build();

                logger.LogRequest(req);

                Future future = asyncClient.prepareRequest(req).execute();
                NettyResponse nettyResponse = (NettyResponse) future.get();

                logger.LogResponse(nettyResponse);
                if (expectedResponseCode != 0) {
                    Assert.assertEquals(nettyResponse.getStatusCode(), expectedResponseCode);
                }

                return nettyResponse.getResponseBody();
            }
            case GET: {
                //append URL paras for http get method
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

                Request req = new RequestBuilder("GET")
                        .setUrl(restUrl)
                        .setHeaders(getHeader(isRoleAPI))
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
                            .setHeaders(getHeader(isRoleAPI))
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

                return nettyResponse.getResponseBody();
            }
            case DELETE: {
                Request req = new RequestBuilder(httpMethod.getHttpMethod())
                        .setUrl(restUrl)
                        .setHeaders(getHeader(isRoleAPI))
                        .build();

                logger.LogRequest(req);

                Future future = asyncClient.prepareRequest(req).execute();
                NettyResponse nettyResponse = (NettyResponse) future.get();

                logger.LogResponse(nettyResponse);
                if (expectedResponseCode != 0) {
                    Assert.assertEquals(nettyResponse.getStatusCode(), expectedResponseCode);
                }

                return nettyResponse.getResponseBody();
            }
            case OPTIONS:
                //TODO
            default:
                throw new TestException(String.format("Unsupported http method found: %s", httpMethod.getHttpMethod()));
        }
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

}
