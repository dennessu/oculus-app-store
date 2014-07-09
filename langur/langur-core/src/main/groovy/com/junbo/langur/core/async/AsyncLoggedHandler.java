/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.async;

import com.ning.http.client.*;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;

/**
 * AsyncLoggedHandler is used to hook and do logging for responses.
 */
public class AsyncLoggedHandler extends AsyncCompletionHandlerBase {
    protected static final Logger logger = JunboAsyncHttpClient.logger;

    private String uri;
    private long startTime = System.currentTimeMillis();
    private ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    private StringBuffer trace = new StringBuffer();
    private boolean is5xxResponse;

    public AsyncLoggedHandler(String uri) {
        this.uri = uri;
    }

    public STATE onBodyPartReceived(final HttpResponseBodyPart content) throws Exception {
        if (logger.isTraceEnabled() || is5xxResponse) {
            bytes.write(content.getBodyPartBytes());
        }
        return super.onBodyPartReceived(content);
    }

    public STATE onStatusReceived(final HttpResponseStatus status) throws Exception {
        is5xxResponse = (status.getStatusCode() / 100) >= 5;
        if (logger.isDebugEnabled() || is5xxResponse) {
            trace.setLength(0);
            bytes.reset();
            trace.append(String.format("HttpResponse: %s\n\tresponse: %s %s", uri, status.getStatusCode(), status.getStatusText()));
        }
        return super.onStatusReceived(status);
    }

    public STATE onHeadersReceived(final HttpResponseHeaders headers) throws Exception {
        if (logger.isTraceEnabled() || is5xxResponse) {
            trace.append("\n\thttp response: \n\theaders:\n");
            for (String key : headers.getHeaders().keySet()) {
                for (String value : headers.getHeaders().get(key)) {
                    trace.append("\t\t");
                    trace.append(key);
                    trace.append(":");
                    trace.append(value);
                    trace.append("\n");
                }
            }
        }
        return super.onHeadersReceived(headers);
    }

    public Response onCompleted(Response response) throws Exception {
        if (logger.isTraceEnabled() || is5xxResponse) {
            trace.append("\n\tresponse body: ");
            trace.append(bytes.toString("UTF-8"));
        }
        if (logger.isDebugEnabled() || is5xxResponse) {
            trace.append("\n\ttime elapsed: " + (System.currentTimeMillis() - startTime));

            if (is5xxResponse) {
                logger.warn(trace.toString());
            } else {
                logger.debug(trace.toString());
            }
            trace.setLength(0);
        }
        return super.onCompleted(response);
    }

    public void onThrowable(Throwable t) {
        if (logger.isDebugEnabled()) {
            trace.append(String.format("\n\tHttpResponse: %s\n\texception: %s", uri, t.getMessage()));
            logger.debug(trace.toString(), t);
            trace.setLength(0);
        }
    }
}
