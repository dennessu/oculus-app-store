/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.InputStreamReader;
import java.util.List;

/**
 * @author dw
 */
public class HttpclientHelper {

    private HttpclientHelper() {

    }

    private static CloseableHttpClient httpclient;

    public static void CreateHttpClient() {
        httpclient = HttpClients.createDefault();
    }

    public static void CloseHttpClient() throws Exception {
        httpclient.close();
    }

    public static void main(String[] args) throws Exception {

    }

    public static CloseableHttpResponse Execute(HttpRequestBase method) throws Exception {
        return httpclient.execute(method);
    }

    public static <T> T Execute(HttpRequestBase method, Class<T> cls) throws Exception {
        CloseableHttpResponse response = httpclient.execute(method);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity responseEntity = response.getEntity();
            if (cls != null) {
                T type = JsonHelper.JsonDeserializer(new InputStreamReader(responseEntity.getContent()), cls);
                EntityUtils.consume(responseEntity);
                return type;
            } else {
                EntityUtils.consume(responseEntity);
                return null;
            }
        } finally {
            response.close();
        }
    }

    public static CloseableHttpResponse GetHttpResponse(
            String requestURI, String objJson, HttpRequestType type, List<NameValuePair> additionalHeaders)
            throws Exception {
        switch (type) {
            case get:
                HttpGet httpGet = new HttpGet(requestURI);
                httpGet.addHeader("Content-Type", "application/json");
                if (additionalHeaders != null && !additionalHeaders.isEmpty()) {
                    for (NameValuePair nvp : additionalHeaders) {
                        httpGet.addHeader(nvp.getName(), nvp.getValue());
                    }
                }
                return Execute(httpGet);
            case post:
                HttpPost httpPost = new HttpPost(requestURI);
                httpPost.addHeader("Content-Type", "application/json");
                if (additionalHeaders != null && !additionalHeaders.isEmpty()) {
                    for (NameValuePair nvp : additionalHeaders) {
                        httpPost.addHeader(nvp.getName(), nvp.getValue());
                    }
                }
                httpPost.setEntity(new StringEntity(objJson, "utf-8"));
                return Execute(httpPost);
            case put:
                HttpPut httpPut = new HttpPut(requestURI);
                httpPut.addHeader("Content-Type", "application/json");
                if (additionalHeaders != null && !additionalHeaders.isEmpty()) {
                    for (NameValuePair nvp : additionalHeaders) {
                        httpPut.addHeader(nvp.getName(), nvp.getValue());
                    }
                }
                httpPut.setEntity(new StringEntity(objJson, "utf-8"));
                return Execute(httpPut);
            case delete:
                HttpDelete httpDelete = new HttpDelete(requestURI);
                httpDelete.addHeader("Content-Type", "application/json");
                if (additionalHeaders != null && !additionalHeaders.isEmpty()) {
                    for (NameValuePair nvp : additionalHeaders) {
                        httpDelete.addHeader(nvp.getName(), nvp.getValue());
                    }
                }
                return Execute(httpDelete);
            default:
                throw new Exception("unexpected httpRequest: " + type);
        }
    }

    public static void ResetHttpClient() throws Exception {
        if (httpclient != null) httpclient.close();
        httpclient = HttpClients.createDefault();
    }

    /**
     * http verb.
     */
    public enum HttpRequestType {
        get,
        post,
        put,
        delete
    }
}
