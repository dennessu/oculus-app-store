/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.libs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ning.http.client.Request;
import com.ning.http.client.providers.netty.NettyResponse;

import java.io.IOException;
import java.util.Map;

/**
 * @author Jason
 * @since 3/10/2014
 * Invoke slf4j log
 */
public class LogHelper {
    private Logger logger;

    public LogHelper(String name) {
        logger = LoggerFactory.getLogger(name);
    }

    public LogHelper(Class clazz) {
        logger = LoggerFactory.getLogger(clazz);
    }

    public void logInfo(String message) {
        logger.info(message);
    }

    public void logError(String message) {
        logger.error(message);
    }

    public void logWarn(String message) {
        logger.warn(message);
    }

    public void LogRequest(Request req) {
        if (req != null) {
            logger.info("**** EXECUTING " + req.getMethod());
            logger.info("**** URI is: " + req.getURI());

            String headers = "**** Headers: [";
            for (Map.Entry eachHeader : req.getHeaders()) {
                headers += eachHeader.getKey() + ": " + eachHeader.getValue() + ", ";
            }
            headers = headers.substring(0, headers.length() - 2) + "]";
            logger.info(headers);

            logger.info("**** Request Body: " + req.getStringData());
        }
        else {
            logger.warn("The request is null");
        }
    }

    public void LogResponse(NettyResponse response) throws IOException {
        if (response != null) {
            logger.info("**** Response status code: " + response.getStatusCode());
            logger.info("**** Response body: " + response.getResponseBody());
        }
        else {
            logger.warn("The response is null");
        }
    }
}
