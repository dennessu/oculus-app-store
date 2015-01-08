/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 SilkCloud and/or its affiliates. All rights reserved.
 */
package com.junbo.common.job.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.cloudant.exception.CloudantConnectException;
import com.junbo.common.shuffle.Oculus40Id;
import com.junbo.common.shuffle.Oculus48Id;
import com.junbo.configuration.ConfigServiceManager;
import com.ning.http.client.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.IOException;

/**
 * SnifferUtils.
 */
public final class SnifferUtils {
    private static ObjectMapper mapper = new ObjectMapper();

    private SnifferUtils() {
        //private ctor
    }

    public static Integer safeParseInt(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        return Integer.parseInt(str);
    }

    public static boolean safeParseBoolean(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return Boolean.parseBoolean(str);
    }

    public static String getConfig(String configKey) {
        return ConfigServiceManager.instance().getConfigValue(configKey);
    }

    public static String isNull(String input, String replace) {
        return input == null ? replace : input;
    }


    public static <T> T parse(String payload, Class<T> clazz) {
        if (payload == null) {
            throw new CloudantConnectException("Response from cloudant DB is invalid");
        }

        try {
            return (T) mapper.readValue(payload, clazz);
        } catch (IOException e) {
            throw new CloudantConnectException("Error occurred while parsing response from cloudant DB", e);
        }
    }

    public static <T> T parse(Response response, Class<T> clazz) {
        if (response == null) {
            throw new CloudantConnectException("Response from cloudant DB is invalid");
        }

        try {
            return parse(response.getResponseBody(), clazz);
        } catch (IOException e) {
            throw new CloudantConnectException("Error occurred while parsing response from cloudant DB", e);
        }
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            //silently ignore
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                //ignore silently
            }
        }
    }

    public static String encodeId(String idStr, String database) {
        try {
            Long longValue = Long.parseLong(idStr);

            // hard code that order id is 40 bit...
            if (database.equalsIgnoreCase("order")) {
                return Oculus40Id.encode(longValue);
            } else {
                return Oculus48Id.encode(longValue);
            }
        } catch (Exception ex) {
            return idStr;
        }
    }

    public static String removePrefix(String str, String prefix) {
        if (str.startsWith(prefix)) {
            return str.substring(prefix.length());
        }
        return str;
    }
}
