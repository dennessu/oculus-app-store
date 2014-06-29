/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.async;

import com.ning.http.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * JunboAsyncHttpClient is used to do logging in request.
 */
public class JunboAsyncHttpClient implements Closeable {
    protected static final Logger logger = LoggerFactory.getLogger(JunboAsyncHttpClient.class);

    private final AsyncHttpClient asyncHttpClient;

    public JunboAsyncHttpClient(AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient;
    }

    /**
     * The BoundRequestBuilder used to build the request.
     */
    public class BoundRequestBuilder extends RequestBuilderBase<BoundRequestBuilder> {

        private BoundRequestBuilder(String reqType, boolean useRawUrl) {
            super(BoundRequestBuilder.class, reqType, useRawUrl);
        }

        private BoundRequestBuilder(Request prototype) {
            super(BoundRequestBuilder.class, prototype);
        }

        public ListenableFuture<Response> execute() throws IOException {
            return JunboAsyncHttpClient.this.executeRequest(build());
        }

        @Override
        public Request build() {
            Request req = super.build();
            if (logger.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("\tHttpRequest: %s %s", req.getMethod(), req.getURI().toString()));
                if (logger.isTraceEnabled()) {
                    sb.append("\n\theaders:");
                    for (String key : req.getHeaders().keySet()) {
                        for (String value : req.getHeaders().get(key)) {
                            sb.append("\n\t\t");
                            sb.append(key);
                            sb.append(":");
                            sb.append(value);
                        }
                    }
                    sb.append("\n\trequest body: ");
                    sb.append(req.getStringData());
                }
                logger.trace(sb.toString());
            }
            return req;
        }
    }

    public void close() {
        asyncHttpClient.close();
    }

    public void closeAsynchronously() {
        asyncHttpClient.closeAsynchronously();
    }

    public boolean isClosed() {
        return asyncHttpClient.isClosed();
    }

    public BoundRequestBuilder prepareGet(String url) {
        return requestBuilder("GET", url);
    }

    public BoundRequestBuilder prepareConnect(String url) {
        return requestBuilder("CONNECT", url);
    }

    public BoundRequestBuilder prepareOptions(String url) {
        return requestBuilder("OPTIONS", url);
    }

    public BoundRequestBuilder prepareHead(String url) {
        return requestBuilder("HEAD", url);
    }

    public BoundRequestBuilder preparePost(String url) {
        return requestBuilder("POST", url);
    }

    public BoundRequestBuilder preparePut(String url) {
        return requestBuilder("PUT", url);
    }

    public BoundRequestBuilder prepareDelete(String url) {
        return requestBuilder("DELETE", url);
    }

    public BoundRequestBuilder prepareRequest(Request request) {
        return requestBuilder(request);
    }

    public ListenableFuture<Response> executeRequest(Request request) throws IOException {
        return asyncHttpClient.executeRequest(request, new AsyncLoggedHandler(request.getURI().toString()));
    }

    public BoundRequestBuilder requestBuilder(String reqType, String url) {
        return new BoundRequestBuilder(reqType, asyncHttpClient.getConfig().isUseRawUrl()).setUrl(url);
    }

    protected BoundRequestBuilder requestBuilder(Request prototype) {
        return new BoundRequestBuilder(prototype);
    }
}
