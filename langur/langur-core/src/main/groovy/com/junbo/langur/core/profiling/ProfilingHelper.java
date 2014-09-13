/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.profiling;

import com.junbo.configuration.ConfigServiceManager;
import com.junbo.langur.core.context.JunboHttpContext;
import com.junbo.langur.core.track.TrackContextManager;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Profile helper.
 */
public class ProfilingHelper {
    private ProfilingHelper() {}

    private static final ThreadLocal<Data> profileData = new ThreadLocal<>();
    private static final String machine = ConfigServiceManager.instance().getConfigContext().getIpAddresses().get(0);
    private static final DateFormat dffull = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public static final String PROFILE_OUTPUT_KEY = "X-Oculus-Profile-Info";

    public static boolean isProfileEnabled() {
        Data data = profileData.get();
        if (data == null) {
            data = new Data();
            profileData.set(data);

            if (TrackContextManager.get().getDebugEnabled()) {
                MultivaluedMap<String, String> headers = JunboHttpContext.getRequestHeaders();
                if (headers != null) {
                    data.profileEnabled = ("true".equalsIgnoreCase(headers.getFirst("X-Enable-Profiling")));
                }
            }
            if (data.profileEnabled) {
                data.init();
            }
        }
        return data.profileEnabled;
    }

    public static void appendRow(Logger logger, String message, Object... args) {
        if (isProfileEnabled()) {
            profileData.get().appendRow(message, args);
        }
        if (logger != null && message != null && logger.isDebugEnabled()) {
            logger.debug(String.format(message, args));
        }
    }

    public static void beginScope(Logger logger, String message, Object... args) {
        if (isProfileEnabled()) {
            profileData.get().beginScope(message, args);
        }
        if (logger != null && message != null && logger.isDebugEnabled()) {
            logger.debug(String.format(message, args));
        }
    }

    public static void endScope(Logger logger, String message, Object... args) {
        if (isProfileEnabled()) {
            profileData.get().endScope(message, args);
        }
        if (logger != null && message != null && logger.isDebugEnabled()) {
            logger.debug(String.format(message, args));
        }
    }

    public static void appendRaw(String rawData) {
        if (isProfileEnabled()) {
            profileData.get().appendRaw(rawData);
        }
    }

    public static boolean hasProfileOutput() {
        Data data = profileData.get();
        if (data == null) {
            return false;
        }
        StringBuffer profileOutput = data.profileOutput;
        return profileOutput != null && profileOutput.length() > 0;
    }

    public static String dumpProfileData() {
        return profileData.get().profileOutput.toString();
    }

    public static void clear() {
        profileData.remove();
    }

    private static class Data {
        boolean profileEnabled;
        StringBuffer profileOutput;
        long startTime;

        void init() {
            Date startDate = new Date();
            profileOutput = new StringBuffer("|" + dffull.format(startDate) + " " + machine + "|");
            startTime = startDate.getTime();
        }

        void appendRow(String message, Object... args) {
            if (message == null) {
                return;
            }

            if (args != null && args.length > 0) {
                message = String.format(message, args);
            }

            long elapsed = System.currentTimeMillis() - startTime;
            profileOutput
                    .append(String.format("%04d", elapsed))
                    .append(" ")
                    .append(message.replace("\r", "\\r").replace("\n", "\\n")).append("|");
        }

        void appendRaw(String rawData) {
            if (StringUtils.isEmpty(rawData)) {
                return;
            }
            profileOutput.append(rawData).append("|");
        }

        void beginScope(String message, Object... args) {
            profileOutput.append("[|");
            appendRow(message, args);
        }

        void endScope(String message, Object... args) {
            appendRow(message, args);
            profileOutput.append("]|");
        }
    }
}
