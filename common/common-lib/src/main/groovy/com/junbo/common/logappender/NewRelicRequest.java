/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.logappender;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by liangfu on 6/25/14.
 */
public class NewRelicRequest {
    private final String base;

    private final String customerId;

    private final String secretKey;

    private TreeMap<String, String> headers;

    private TreeMap<String, String> params;

    private URL url;

    private String body;

    public NewRelicRequest(String base, String customerId, String secretKey) {
        this.base = base;
        this.customerId = customerId;
        this.secretKey = secretKey;
        headers = new TreeMap<>();
        params = new TreeMap<>();
        buildHeaders();
        buildParams();
        buildURL();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addParam(String key, String value) {
        params.put(key, value);
    }

    public void setPostBody(String postBody) {

        body = postBody;
    }

    public String getPostBody() {

        return body;
    }

    public Map<String, String> getAllHeaders() {
        return headers;
    }

    public Map<String, String> getAllParams() {

        return params;
    }

    public String executeRequest() throws IOException {
        StringBuffer fullURL = new StringBuffer(url.toString());

        if (params.size() > 0) {
            fullURL.append("?");
            int i = 0;
            for (String key : params.keySet()) {
                if (++i != 0) {
                    fullURL.append("&");
                }
                fullURL.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(params.get(key), "UTF-8"));
            }
        }

        URL fullUrl = new URL(fullURL.toString());
        HttpsURLConnection con = (HttpsURLConnection) fullUrl.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");

        for(Map.Entry<String, String> entry : headers.entrySet()) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
        }

        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(body);
        wr.flush();
        wr.close();

        int response = con.getResponseCode();
        BufferedReader in;
        String urlOutput = "";

        try {
            InputStream isr = (response == 200) ? con.getInputStream() : con.getErrorStream();
            in = new BufferedReader(new InputStreamReader(isr));
            String urlReturn;

            while ((urlReturn = in.readLine()) != null) {
                urlOutput += urlReturn;
            }
            in.close();
        }
        catch (IOException e) {
            System.err.println("IOException while reading from input stream " + e.getMessage());
        }

        return urlOutput;
    }

    private void buildURL() {
        try {
            url = new URL(base + "/" + customerId + "/events");
        } catch (MalformedURLException ex) {
            System.err.println("URL exception " + ex.getMessage());
        }
    }

    private void buildHeaders() {
        headers.put("Content-Type", "application/json");
        headers.put("X-Insert-Key", secretKey);
    }

    private void buildParams() {
        // do nothing here
    }
}
