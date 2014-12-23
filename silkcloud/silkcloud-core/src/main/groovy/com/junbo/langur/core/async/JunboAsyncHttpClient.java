/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.async;

import com.junbo.common.filter.SequenceIdFilter;
import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;
import com.junbo.langur.core.profiling.ProfilingHelper;
import com.junbo.langur.core.promise.Promise;
import com.ning.http.client.*;
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.io.Closeable;
import java.io.IOException;

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture;

/**
 * JunboAsyncHttpClient is used to do logging in request.
 */
public class JunboAsyncHttpClient implements Closeable {
    protected static final Logger logger = LoggerFactory.getLogger(JunboAsyncHttpClient.class);

    private final AsyncHttpClient asyncHttpClient;
    private boolean isDebugMode;

    private static JunboAsyncHttpClient instance = new JunboAsyncHttpClient();
    public static JunboAsyncHttpClient instance() {
        return instance;
    }

    private JunboAsyncHttpClient() {
        ConfigService configService = ConfigServiceManager.instance();

        AsyncHttpClientConfigBean config = new AsyncHttpClientConfigBean();
        NettyAsyncHttpProviderConfig nettyConfig = new NettyAsyncHttpProviderConfig();
        int maxHeadersSize = Integer.parseInt(configService.getConfigValue("common.client.maxHeadersSize"));
        nettyConfig.addProperty(NettyAsyncHttpProviderConfig.HTTP_CLIENT_CODEC_MAX_HEADER_SIZE, maxHeadersSize);
        nettyConfig.addProperty(NettyAsyncHttpProviderConfig.HTTPS_CLIENT_CODEC_MAX_HEADER_SIZE, maxHeadersSize);

        config.setConnectionTimeOutInMs(Integer.parseInt(configService.getConfigValue("common.client.connectionTimeout")));
        config.setRequestTimeoutInMs(Integer.parseInt(configService.getConfigValue("common.client.requestTimeout")));
        config.setMaxConnectionPerHost(Integer.parseInt(configService.getConfigValue("common.client.maxConnectionPerHost")));
        config.setCompressionEnabled(true);
        config.setProviderConfig(nettyConfig);

        this.asyncHttpClient = new AsyncHttpClient(new UTF8NettyAsyncHttpProvider(config), config);
        this.isDebugMode = "true".equalsIgnoreCase(configService.getConfigValue("common.conf.debugMode"));
    }

    public AsyncHttpClient getAsyncHttpClient() {
        return asyncHttpClient;
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

        public Promise<Response> execute() throws IOException {
            return JunboAsyncHttpClient.this.executeRequest(build());
        }

        @Override
        public Request build() {
            super.setBodyEncoding("UTF-8");
            Request req = super.build();
            if (logger.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("\tHttpRequest: %s %s %s", req.getMethod(), req.getURI().toString(), MDC.get(SequenceIdFilter.X_REQUEST_ID)));
                if (logger.isTraceEnabled()) {
                    sb.append("\n\theaders:");
                    for (String key : req.getHeaders().keySet()) {
                        if (!isDebugMode) {
                            if ("Authorization".equals(key)) {
                                sb.append("\n\t\t");
                                sb.append(key);
                                sb.append(":");
                                sb.append("**masked**");
                                continue;
                            }
                        }
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
                logger.debug(sb.toString());
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

    public Promise<Response> executeRequest(final Request request) throws IOException {
        ProfilingHelper.begin("HTTP", "%s %s", request.getMethod(), request.getURI());
        try {
            Promise<Response> response = Promise.wrap(asGuavaFuture(
                    asyncHttpClient.executeRequest(request, new AsyncLoggedHandler(request.getMethod(),
                            request.getURI(), MDC.get(SequenceIdFilter.X_REQUEST_ID)))));
            if (ProfilingHelper.isProfileEnabled()) {
                response = response.then(new Promise.Func<Response, Promise<Response>>() {
                    @Override
                    public Promise<Response> apply(Response response) {
                        if (ProfilingHelper.isProfileEnabled()) {
                            String profilingOutput = response.getHeaders().getJoinedValue(ProfilingHelper.PROFILE_OUTPUT_KEY, "");
                            if (!StringUtils.isEmpty(profilingOutput)) {
                                ProfilingHelper.appendRaw("[|");
                                ProfilingHelper.appendRaw(profilingOutput);
                                ProfilingHelper.appendRaw("]|");
                            }
                        }
                        ProfilingHelper.end("(%s) %d", response.getStatusText(), response.getStatusCode());
                        return Promise.pure(response);
                    }
                }).recover(new Promise.Func<Throwable, Promise<Response>>() {
                    @Override
                    public Promise<Response> apply(Throwable t) {
                        ProfilingHelper.err(t);
                        if (t instanceof RuntimeException) {
                            throw (RuntimeException) t;
                        }
                        throw new RuntimeException(t);
                    }
                });
            }
            return response;
        } catch (Throwable ex) {
            ProfilingHelper.err(ex);
            throw ex;
        }
    }

    public BoundRequestBuilder requestBuilder(String reqType, String url) {
        return new BoundRequestBuilder(reqType, asyncHttpClient.getConfig().isUseRawUrl()).setUrl(url);
    }

    protected BoundRequestBuilder requestBuilder(Request prototype) {
        return new BoundRequestBuilder(prototype);
    }
}
