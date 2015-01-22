/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment.apihelper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.test.common.apihelper.Header;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.payment.apihelper.PaymentProviderService;
import com.ning.http.client.*;
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig;
import com.ning.http.client.providers.netty.NettyResponse;
import org.apache.http.client.HttpResponseException;
import org.testng.Assert;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Cloud on 14-12-23.
 */
public class PaymentProviderServiceImpl extends HttpClientBase implements PaymentProviderService {
    private static final String requestUrl = String.format("http://www.facebook.com/payments/generate_token");

    private static PaymentProviderService instance;

    public static synchronized PaymentProviderService getInstance() {
        if (instance == null) {
            instance = new PaymentProviderServiceImpl();
        }
        return instance;
    }

    protected AsyncHttpClient getAsyncHttpClient() {

        AsyncHttpClientConfigBean config = new AsyncHttpClientConfigBean();
        NettyAsyncHttpProviderConfig nettyConfig = new NettyAsyncHttpProviderConfig();
        int maxHeadersSize = 65536;
        nettyConfig.addProperty(NettyAsyncHttpProviderConfig.HTTP_CLIENT_CODEC_MAX_HEADER_SIZE, maxHeadersSize);
        nettyConfig.addProperty(NettyAsyncHttpProviderConfig.HTTPS_CLIENT_CODEC_MAX_HEADER_SIZE, maxHeadersSize);
        //config.setProxyServer(new ProxyServer("127.0.0.1",13128));
        config.setProviderConfig(nettyConfig);
        config.setMaxRequestRetry(3);

        return new AsyncHttpClient(config);
    }

    protected FluentCaseInsensitiveStringsMap getHeader(boolean isServiceScope, List<String> headersToRemove) {
        FluentCaseInsensitiveStringsMap headers = new FluentCaseInsensitiveStringsMap();
        headers.add(Header.USER_AGENT, "curl/7.30.0");
        headers.add(Header.CONTENT_TYPE, "text/plain");
        return headers;
    }

    @Override
    public String getToken(String cardNumber, String csc) throws Exception {
        return getToken(cardNumber, csc, 200);
    }

    @Override
    public String getToken(String cardNumber, String csc, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.POST, requestUrl, String.format("creditCardNumber=%s&csc=%s", cardNumber, csc));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(responseBody).get("token").textValue();
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

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


    public String post(String url, String content)
            throws Exception {
        String result = null;

        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, new TrustManager[]{myX509TrustManager}, null);

        URL requestUrl = new URL(url);
        HttpsURLConnection httpsConn = (HttpsURLConnection) requestUrl.openConnection(Proxy.NO_PROXY);
        httpsConn.setSSLSocketFactory(sslcontext.getSocketFactory());
        httpsConn.setHostnameVerifier(new TrustAnyHostnameVerifier());
        httpsConn.setRequestProperty("User-Agent", "curl/7.30.0");
        httpsConn.setRequestProperty("Content-Type", "text/plain");
        httpsConn.setRequestMethod("POST");
        httpsConn.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(
                httpsConn.getOutputStream());
        if (content != null)
            out.writeBytes(content);

        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
        int code = httpsConn.getResponseCode();
        if (HttpsURLConnection.HTTP_OK == code) {
            String temp = in.readLine();
            while (temp != null) {
                if (result != null)
                    result += temp;
                else
                    result = temp;
                temp = in.readLine();
            }
        }
        return result;
    }

    protected String restApiCall(HTTPMethod httpMethod, String restUrl, String requestBody,
                                 int expectedResponseCode, HashMap<String, List<String>> httpParameters,
                                 boolean isServiceScope, List<String> headersToRemove) throws Exception {
        switch (httpMethod) {
            case PUT:
            case POST: {
                try {
                    Request req;
                    ProxyServer proxyServer;
                    String profile = System.getProperty("profile", "onebox");
                    if (profile.equalsIgnoreCase("onebox")) {
                        proxyServer = new ProxyServer(ProxyServer.Protocol.HTTP, "127.0.0.1", 13128, "silkcloud", "#Bugs4$1");
                    }
                    else {
                        proxyServer = null;
                    }

                    if (Master.getInstance().getCookies().size() > 0) {
                        req = new RequestBuilder(httpMethod.getHttpMethod())
                                .setUrl(restUrl)
                                .setHeaders(getHeader(isServiceScope, headersToRemove))
                                .setBodyEncoding("UTF-8")
                                .setBody(requestBody)
                                .addCookie(Master.getInstance().getCookies().get(0))
                                .build();
                    } else {
                        RequestBuilder reqBuilder = new RequestBuilder(httpMethod.getHttpMethod())
                                .setUrl(restUrl)
                                .setHeaders(getHeader(isServiceScope, headersToRemove))
                                .setBodyEncoding("UTF-8")
                                .setBody(requestBody);
                        if (proxyServer != null) {
                            reqBuilder.setProxyServer(proxyServer);
                        }
                        req = reqBuilder.build();
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

            case OPTIONS:
                //TODO
            default:
                throw new TestException(String.format("Unsupported http method found: %s", httpMethod.getHttpMethod()));
        }
    }


}
