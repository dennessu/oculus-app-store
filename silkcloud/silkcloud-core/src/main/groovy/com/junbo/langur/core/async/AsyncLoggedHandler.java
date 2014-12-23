/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.async;

import com.junbo.common.filter.SequenceIdFilter;
import com.ning.http.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AsyncLoggedHandler is used to hook and do logging for responses.
 */
public class AsyncLoggedHandler extends AsyncCompletionHandlerBase {
    protected static final Logger logger = JunboAsyncHttpClient.logger;
    protected static final Logger HTTPD_LOGGER = LoggerFactory.getLogger(AsyncLoggedHandler.class);
    private static final SimpleDateFormatThreadLocal DATE_FORMAT =
            new SimpleDateFormatThreadLocal("[yyyy-MM-dd HH:mm:ss.SSS Z]");
    private static final String COUCH_REQUEST_ID = "X-Couch-Request-ID";
    private static final String COUCH_LOCATION = "Location";
    private static final Pattern ACCESS_TOKEN_PATTERN = Pattern.compile("access_token=[^&]+");

    private String method;
    private URI uri;
    private String requestId;
    private long startTime = System.currentTimeMillis();
    private ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    private StringBuffer trace = new StringBuffer();
    private boolean is5xxResponse;

    public AsyncLoggedHandler(String method, URI uri, String requestId) {
        this.method = method;
        this.uri = maskUri(uri);
        this.requestId = requestId;
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
            trace.append(String.format("HttpResponse: %s %s\n\tresponse: %s %s", method, uri.toString(), status.getStatusCode(), status.getStatusText()));
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

            try (RequestIdScope scope = new RequestIdScope(requestId)) {
                if (is5xxResponse) {
                    logger.warn(trace.toString());
                } else {
                    logger.debug(trace.toString());
                }
                trace.setLength(0);
            }
        }
        HTTPD_LOGGER.info(accessLog(response));
        return super.onCompleted(response);
    }

    public void onThrowable(Throwable t) {
        try (RequestIdScope scope = new RequestIdScope(requestId)) {
            logger.error("Http Exception: %s", uri.toString(), t);
            if (logger.isDebugEnabled()) {
                trace.append(String.format("\n\tHttpResponse: %s\n\texception: %s", uri.toString(), t.getMessage()));
                logger.debug(trace.toString(), t);
                trace.setLength(0);
            }
        }
    }

    private String accessLog(Response response) {
        Long latency = System.currentTimeMillis() - startTime;
        String responseTimestamp = DATE_FORMAT.get().format(new Date(System.currentTimeMillis()));
        String couchRequestId = response.getHeader(COUCH_REQUEST_ID);

        String couchLocation = "";
        if ("POST".equalsIgnoreCase(method)) {
            // extract the response ID for POST calls to cloudant
            couchLocation = response.getHeader(COUCH_LOCATION);
            if (couchLocation == null) {
                couchLocation = "";
            }
            // when found and / is the last charactor,
            //      couchLocation.lastIndexOf('/') + 1 == couchLocation.size(), it will not out of bound.
            // when not found, lastIndexOf return -1 substring(0) is also good.
            // so it will never out of bound
            couchLocation = couchLocation.substring(couchLocation.lastIndexOf('/') + 1);
        }

        return String.format("- %d - %s \"%s %s %s\" %d - \"-\" \"Ning Async Http Client\" \"[]\" [%s][%s][%s]",
                latency, responseTimestamp, method, uri.toString(), uri.getScheme(), response.getStatusCode(),
                requestId == null ? "" : requestId,
                couchRequestId == null ? "" : couchRequestId,
                couchLocation);
    }

    private static URI maskUri(URI uri) {
        // mask all access tokens in the URI parameters
        Matcher matcher = ACCESS_TOKEN_PATTERN.matcher(uri.toString());
        boolean result = matcher.find();
        if (result) {
            StringBuffer sb = new StringBuffer();
            do {
                matcher.appendReplacement(sb, "access_token=******");
                result = matcher.find();
            } while (result);
            matcher.appendTail(sb);

            try {
                return new URI(sb.toString());
            } catch (Exception ex) {
                logger.error("Invalid URI string: " + sb.toString(), ex);
                return uri;
            }
        }
        return uri;
    }

    static class SimpleDateFormatThreadLocal extends ThreadLocal<SimpleDateFormat> {

        private final SimpleDateFormat format;

        SimpleDateFormatThreadLocal(String format) {
            this.format = new SimpleDateFormat(format, Locale.ENGLISH);
        }

        @Override
        protected SimpleDateFormat initialValue() {
            return (SimpleDateFormat) format.clone();
        }
    }

    static class RequestIdScope implements AutoCloseable {
        private String oldRequestId;

        public RequestIdScope(String requestId) {
            oldRequestId = MDC.get(SequenceIdFilter.X_REQUEST_ID);
            MDC.put(SequenceIdFilter.X_REQUEST_ID, requestId);
        }

        @Override
        public void close() {
            if (oldRequestId != null) {
                MDC.put(SequenceIdFilter.X_REQUEST_ID, oldRequestId);
            } else {
                MDC.remove(SequenceIdFilter.X_REQUEST_ID);
            }
        }
    }
}
