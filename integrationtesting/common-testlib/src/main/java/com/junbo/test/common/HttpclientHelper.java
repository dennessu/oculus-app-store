/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common;

//import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @author dw
 */
public class HttpclientHelper {

    private HttpclientHelper() {

    }

    public static void main(String[] args) throws Exception {
//        SimpleGet("http://localhost:8081/rest/users");

//        String userName = RandomHelper.randomAlphabetic(12);
//        String objJson = "{\"userName\": \"" + userName
//                + "\",\"password\": \"dummypassword\",\"status\": \"ACTIVE\"}";
//        SimpleJsonPost("http://localhost:8081/rest/users", objJson);
    }

    public static <T> T SimpleGet(String requestURI, Class<T> cls) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(requestURI);
            httpGet.addHeader("Content-Type", "application/json");
            CloseableHttpResponse response = httpclient.execute(httpGet);

            try {
                System.out.println(response.getStatusLine());
                HttpEntity entity = response.getEntity();

//                BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
//                String readLine;
//                while (((readLine = br.readLine()) != null)) {
//                    System.err.println(readLine);
//                }
                T type = JsonHelper.JsonDeserializer(new InputStreamReader(entity.getContent()), cls);
                EntityUtils.consume(entity);
                return type;
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

    public static <T> T SimpleJsonPost(String requestURI, String objJson, Class<T> cls) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            StringEntity se = new StringEntity(objJson);
            se.setContentType("application/json");
            HttpPost httpPost = new HttpPost(requestURI);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(se);
            CloseableHttpResponse response = httpclient.execute(httpPost);

            try {
                System.out.println(response.getStatusLine());
                HttpEntity entity = response.getEntity();

//                BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
//                String readLine;
//                while (((readLine = br.readLine()) != null)) {
//                    System.err.println(readLine);
//                }
//                Object obj = GsonHelper.GsonDeserializer(
//                        new InputStreamReader(entity.getContent()), cls);
                T type = JsonHelper.JsonDeserializer(new InputStreamReader(entity.getContent()), cls);
                EntityUtils.consume(entity);
                return type;
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }
}
