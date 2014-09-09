/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.common;


import javax.net.ssl.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by weiyu_000 on 9/9/14.
 */
public class HttpClientHelper {

    public static void validateURLAccessibility(String url, int expectedResponseCode) throws Exception {
        URL requestUrl = new URL(url);
        if(url.startsWith("https")){
        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, new TrustManager[]{myX509TrustManager}, null);

        HttpsURLConnection httpsConn = (HttpsURLConnection) requestUrl.openConnection();
        httpsConn.setSSLSocketFactory(sslcontext.getSocketFactory());
        httpsConn.setHostnameVerifier(new NullHostnameVerifier());
        assert  httpsConn.getResponseCode() == expectedResponseCode;}
        else {
            HttpURLConnection httpURLConnection = (HttpURLConnection) requestUrl.openConnection();
            assert httpURLConnection.getResponseCode() == expectedResponseCode;
        }

    }

    private static class NullHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static TrustManager myX509TrustManager = new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    };

}
