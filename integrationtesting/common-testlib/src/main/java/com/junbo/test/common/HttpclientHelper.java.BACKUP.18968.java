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
            HttpEntity entity = response.getEntity();
            T type = JsonHelper.JsonDeserializer(new InputStreamReader(entity.getContent()), cls);
            EntityUtils.consume(entity);
            return type;
        } finally {
            response.close();
        }
    }

<<<<<<< HEAD
=======
    public static CloseableHttpResponse SimplePost(String requestURI, List<NameValuePair> nvps) throws Exception {
        return SimplePost(requestURI, nvps, true);
    }

    public static CloseableHttpResponse SimplePost(String requestURI, List<NameValuePair> nvps, Boolean enableRedirect)
            throws Exception {
        HttpPost httpost = new HttpPost(requestURI);
        httpost.setConfig(RequestConfig.custom().setRedirectsEnabled(enableRedirect).build());
        httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        CloseableHttpResponse response = httpclient.execute(httpost);

        System.out.println("Response Status line :" + response.getStatusLine());
        return response;
    }

    public static String SimplePostWithRedirect(String requestURI, List<NameValuePair> nvps) throws Exception {
        HttpPost httpost = new HttpPost(requestURI);
        httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

        HttpContext context = new BasicHttpContext();
        CloseableHttpResponse response = httpclient.execute(httpost, context);
        try {
            HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
            HttpHost currentHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
            String currentUrl = (currentReq.getURI().isAbsolute()) ?
                    currentReq.getURI().toString() : (currentHost.toURI() + currentReq.getURI());
            return currentUrl;
        } finally {
            response.close();
        }
    }

    public static <T> T SimpleJsonPost(String requestURI, String objJson, Class<T> cls) throws Exception {
        StringEntity se = new StringEntity(objJson, "utf-8");
        se.setContentType("application/json");
        return SimpleJsonPost(requestURI, se, cls);
    }

    public static <T> T SimpleJsonPost(String requestURI, HttpEntity entity, Class<T> cls) throws Exception {
        HttpPost httpPost = new HttpPost(requestURI);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity responseEntity = response.getEntity();

//                BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
//                String readLine;
//                while (((readLine = br.readLine()) != null)) {
//                    System.err.println(readLine);
//                }
//                Object obj = GsonHelper.GsonDeserializer(
//                        new InputStreamReader(entity.getContent()), cls);
            T type = JsonHelper.JsonDeserializer(new InputStreamReader(responseEntity.getContent()), cls);
            EntityUtils.consume(responseEntity);
            return type;
        } finally {
            response.close();
        }
    }

    public static <T> T SimpleHttpPost(HttpPost httpPost, Class<T> cls) throws Exception {
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity responseEntity = response.getEntity();
            T type = JsonHelper.JsonDeserializer(new InputStreamReader(responseEntity.getContent()), cls);
            EntityUtils.consume(responseEntity);
            return type;
        } finally {
            response.close();
        }
    }

    public static <T> T SimpleJsonPut(String requestURI, String objJson, Class<T> cls) throws Exception {
        HttpPut httpPut = new HttpPut(requestURI);
        httpPut.addHeader("Content-Type", "application/json");
        httpPut.setEntity(new StringEntity(objJson, "utf-8"));
        CloseableHttpResponse response = httpclient.execute(httpPut);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity responseEntity = response.getEntity();
            T type = JsonHelper.JsonDeserializer(new InputStreamReader(responseEntity.getContent()), cls);
            EntityUtils.consume(responseEntity);
            return type;
        } finally {
            response.close();
        }
    }

    public static <T> T SimpleHttpPut(HttpPut httpPut, Class<T> cls) throws Exception {
        CloseableHttpResponse response = httpclient.execute(httpPut);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity responseEntity = response.getEntity();
            T type = JsonHelper.JsonDeserializer(new InputStreamReader(responseEntity.getContent()), cls);
            EntityUtils.consume(responseEntity);
            return type;
        } finally {
            response.close();
        }
    }

    public static void SimpleDelete(String requestURI) throws Exception {
        HttpDelete httpDelete = new HttpDelete(requestURI);
        httpDelete.addHeader("Content-Type", "application/json");
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        try {
            Assert.assertEquals(
                    true,
                    (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 412),
                    "validate HttpDelete response is 200 or 412 (when item not found)");
        } finally {
            response.close();
        }
    }

    public static void SimpleHttpDelete(HttpDelete httpDelete) throws Exception {
        CloseableHttpResponse response = httpclient.execute(httpDelete);
        try {
            Assert.assertEquals(
                    true,
                    (response.getStatusLine().getStatusCode() == 204 || response.getStatusLine().getStatusCode() == 412),
                    "validate HttpDelete response is 200 or 412 (when item not found)");
        } finally {
            response.close();
        }
    }

    public static CloseableHttpResponse PureHttpResponse(String requestURI, String objJson, HttpRequestType type)
            throws Exception {
        return PureHttpResponse(requestURI, objJson, type, null);
    }

>>>>>>> Fix bug 662&& 632
    public static CloseableHttpResponse PureHttpResponse(
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
