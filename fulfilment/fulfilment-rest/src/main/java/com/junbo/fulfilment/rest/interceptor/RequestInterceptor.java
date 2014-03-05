/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.rest.interceptor;

import org.apache.commons.io.IOUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RequestInterceptor.
 */
public class RequestInterceptor implements ReaderInterceptor {
    private static final Pattern PATTERN = Pattern.compile("(\".*?\")\\s*?:.*?\\{\"href\".*?\"id\"\\s*:\\s*(.*?)\\}");

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        String request = stream2String(context.getInputStream());
        context.setInputStream(string2Stream(cleanup(request)));

        return context.proceed();
    }

    public String stream2String(InputStream stream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream, writer, "UTF-8");

        return writer.toString();
    }

    public InputStream string2Stream(String payload) {
        return new ByteArrayInputStream(payload.getBytes());
    }

    public String cleanup(String payload) {
        Matcher matcher = PATTERN.matcher(payload);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);

            matcher.appendReplacement(sb, key + ": " + value);
        }

        matcher.appendTail(sb);
        return sb.toString();
    }
}
