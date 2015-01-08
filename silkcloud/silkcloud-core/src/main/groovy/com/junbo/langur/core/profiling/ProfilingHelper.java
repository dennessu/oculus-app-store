/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.profiling;

import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;
import com.junbo.langur.core.context.JunboHttpContext;
import com.junbo.langur.core.metric.MetricHelper;
import com.junbo.langur.core.track.TrackContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Profile helper.
 */
public class ProfilingHelper {
    private ProfilingHelper() {
    }

    private static final Logger logger = LoggerFactory.getLogger(ProfilingHelper.class);
    private static final ThreadLocal<Data> profileData = new ThreadLocal<>();
    private static final String machine = ConfigServiceManager.instance().getConfigContext().getIpAddresses().get(0);
    private static final DateFormat dffull = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public static final String PROFILE_OUTPUT_KEY = "X-Oculus-Profile-Info";

    private static class Thresholds {
        int debugThreshold;
        int warnThreshold;

        public Thresholds(Integer debugThreshold, Integer warnThreshold) {
            this.debugThreshold = debugThreshold;
            this.warnThreshold = warnThreshold;
        }
    }

    private static boolean profilingLoggerEnabled;
    private static Map<String, Thresholds> defaultThresholdsByType = new HashMap<>();
    private static Thresholds defaultThresholds;

    static {
        ConfigService configService = ConfigServiceManager.instance();

        profilingLoggerEnabled = Boolean.parseBoolean(configService.getConfigValue("common.profiling.loggerEnabled"));

        String defaultThresholdConfig = configService.getConfigValue("common.profiling.defaultThreshold");
        String defaultWarnThresholdConfig = configService.getConfigValue("common.profiling.defaultWarnThreshold");
        defaultThresholds = new Thresholds(
                Integer.parseInt(defaultThresholdConfig),
                Integer.parseInt(defaultWarnThresholdConfig));

        String defaultThresholdsPerTypeConfig = configService.getConfigValue("common.profiling.defaultThresholdsPerType");
        for (String defaultThresholdPerType : defaultThresholdsPerTypeConfig.split(",")) {
            defaultThresholdPerType = defaultThresholdPerType.trim();
            if (StringUtils.isEmpty(defaultThresholdPerType)) {
                continue;
            }
            String[] defaultThresholdPerTypeSplit = defaultThresholdPerType.split(":");
            if (defaultThresholdPerTypeSplit.length != 3) {
                logger.error("Invalid threshold per type setting: " + defaultThresholdPerType);
                continue;
            }
            defaultThresholdsByType.put(defaultThresholdPerTypeSplit[0], new Thresholds(
                    Integer.parseInt(defaultThresholdPerTypeSplit[1]),
                    Integer.parseInt(defaultThresholdPerTypeSplit[2])
            ));
        }
    }

    private static Integer getOverrideProfilingThreshold() {
        MultivaluedMap<String, String> headers = JunboHttpContext.getRequestHeaders();
        if (headers != null) {
            String header = headers.getFirst("X-Enable-Profiling");
            if (!StringUtils.isEmpty(header)) {
                if ("true".equalsIgnoreCase(header)) {
                    return 0;
                } else {
                    try {
                        return Integer.parseInt(header);
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public static boolean hasProfileHeader() {
        MultivaluedMap<String, String> headers = JunboHttpContext.getRequestHeaders();
        if (headers != null) {
            String header = headers.getFirst("X-Enable-Profiling");
            return !StringUtils.isEmpty(header);
        }
        return false;
    }

    public static boolean isProfileEnabled() {
        Data data = getOrInitProfileData();
        return data.profilingEnabled;
    }

    public static boolean isProfileOutputEnabled() {
        Data data = getOrInitProfileData();
        return data.profilingOutputEnabled;
    }

    private static Data getOrInitProfileData() {
        Data data = profileData.get();
        if (data == null) {
            data = new Data();
            profileData.set(data);

            if (profilingLoggerEnabled) {
                data.profilingEnabled = true;
            }

            if (TrackContextManager.get().getDebugEnabled()) {
                Integer profilingThreshold = getOverrideProfilingThreshold();
                if (profilingThreshold != null) {
                    data.profilingOutputEnabled = true;
                    data.overrideThreshold = profilingThreshold;
                }
            }
            data.profilingEnabled = profilingLoggerEnabled || data.profilingOutputEnabled;
            data.init();
        }
        return data;
    }

    public static void begin(String type, String message, Object... args) {
        if (isProfileEnabled()) {
            profileData.get().begin(type, message, args);
        }
    }

    public static void end(String message, Object... args) {
        if (isProfileEnabled()) {
            profileData.get().end(null, message, args);
        }
    }

    public static void end(Logger logger, String message, Object... args) {
        if (isProfileEnabled()) {
            profileData.get().end(logger, message, args);
        }
    }

    public static void err(Throwable ex) {
        err(null, ex);
    }

    public static void err(Logger logger, Throwable ex) {
        if (isProfileEnabled()) {
            if (ex != null) {
                profileData.get().end(logger, true, "(ERR) %s %s", ex.getClass().getSimpleName(), ex.getMessage());
            } else {
                profileData.get().end(logger, true, "(ERR) null");
            }
        }
    }

    public static void appendRow(Logger logger, String message, Object... args) {
        if (isProfileEnabled()) {
            profileData.get().appendRow(message, args);
        }
        if (logger != null && message != null && logger.isDebugEnabled()) {
            logger.debug(format(message, args));
        }
    }

    public static void beginScope(String type, String message, Object... args) {
        if (isProfileEnabled()) {
            profileData.get().beginScope(type, message, args);
        }
    }

    public static void endScope(String message, Object... args) {
        if (isProfileEnabled()) {
            profileData.get().endScope(null, message, args);
        }
    }

    public static void endScope(Logger logger, String message, Object... args) {
        if (isProfileEnabled()) {
            profileData.get().endScope(logger, message, args);
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
        profileData.get().drain();
        return profileData.get().profileOutput.toString();
    }

    public static String prettyPrint(String data) {
        StringBuilder builder = new StringBuilder(data.length() * 2);

        int indent = 0;
        for (String line : data.split("\\|")) {
            if (line.equals("]")) indent--;
            for (int i = 0; i < indent; ++i) {
                builder.append("    ");
            }
            builder.append(line).append("\n");
            if (line.equals("[")) indent++;
        }
        return builder.toString();
    }

    public static void clear() {
        profileData.remove();
    }

    private static class DataStackFrame {
        long startTime;
        String type;
        String message;

        public DataStackFrame(long startTime, String type, String message) {
            this.startTime = startTime;
            this.type = type;
            this.message = message;
        }
    }

    private static class Data {
        boolean profilingEnabled;
        boolean profilingOutputEnabled;
        int overrideThreshold;
        StringBuffer profileOutput;

        long profilingStartTime;
        Stack<DataStackFrame> dataStack;
        long lastScopeStartTime;

        void init() {
            dataStack = new Stack<>();
            Date startDate = new Date();
            profilingStartTime = startDate.getTime();

            if (profilingOutputEnabled) {
                profileOutput = new StringBuffer("|" + dffull.format(startDate) + " " + machine + "|");
            }
        }

        void begin(String type, String message, Object... args) {
            if (message == null) {
                return;
            }
            message = format(message, args);
            dataStack.push(new DataStackFrame(System.currentTimeMillis(), type, message));
        }

        void end(Logger logger, String result, Object... args) {
            end(logger, false, result, args);
        }

        void end(Logger logger, boolean hasError, String result, Object... args) {
            if (result == null) {
                return;
            }
            if (logger == null) {
                logger = ProfilingHelper.logger;
            }
            result = format(result, args);

            long now = System.currentTimeMillis();
            if (!dataStack.isEmpty()) {
                DataStackFrame stackFrame = dataStack.pop();

                long startOffset = stackFrame.startTime - profilingStartTime;
                long stepElapsed = now - stackFrame.startTime;

                if (hasError) {
                    MetricHelper.addMetric(stackFrame.type, "ERR", stepElapsed);
                } else {
                    MetricHelper.addMetric(stackFrame.type, result, stepElapsed);
                }

                Thresholds thresholds = getThreshold(stackFrame.type);

                if (stepElapsed >= overrideThreshold ||
                        stepElapsed >= thresholds.debugThreshold ||
                        logger.isTraceEnabled()) {
                    String traceLine = new StringBuffer()
                            .append(String.format("%04d", startOffset))
                            .append(" ")
                            .append(String.format("%04d", stepElapsed))
                            .append(" (").append(stackFrame.type).append(") ")
                            .append(stackFrame.message).append(" ").append(result)
                            .toString();
                    if (profilingLoggerEnabled) {
                        if (stepElapsed >= thresholds.debugThreshold) {
                            // If the elapse is warning level, still trace at debug level when external logger is used.
                            // This is to ensure the external logger get full data for the latency it is trying to track.
                            if (stepElapsed < thresholds.warnThreshold || logger != ProfilingHelper.logger) {
                                logger.debug(traceLine);
                            }
                            // Otherwise it's unnecessary to trace the log using both debug and warning level.
                            // The if branch below will take care of the logging.
                        } else {
                            // Trace at trace level anyway
                            logger.trace(traceLine);
                        }
                        if (stepElapsed >= thresholds.warnThreshold) {
                            // for warnings, always write the log to ProfilingHelper logger.
                            ProfilingHelper.logger.warn(traceLine);
                        }
                    }
                    if (profilingOutputEnabled) {
                        boolean needOutput;
                        if (overrideThreshold == -1) {
                            needOutput = stepElapsed >= thresholds.debugThreshold;
                        } else {
                            needOutput = stepElapsed >= overrideThreshold;
                        }
                        if (needOutput) {
                            profileOutput.append(traceLine.replace("\r", "\\r").replace("\n", "\\n")).append("|");
                        }
                    }
                }
            } else {
                logger.error("end() without corresponding start(). Message: " + result);
            }
        }

        void appendRow(String message, Object... args) {
            if (message == null) {
                return;
            }
            message = format(message, args);
            long now = System.currentTimeMillis();
            long startOffset = now - profilingStartTime;
            if (profilingOutputEnabled) {
                profileOutput
                        .append(String.format("%04d", startOffset))
                        .append(" 0000")
                        .append((" (Log) " + message).replace("\r", "\\r").replace("\n", "\\n")).append("|");
            }
        }

        void appendRaw(String rawData) {
            if (profilingOutputEnabled) {
                if (StringUtils.isEmpty(rawData)) {
                    return;
                }
                profileOutput.append(rawData).append("|");
            }
        }

        void beginScope(String type, String message, Object... args) {
            lastScopeStartTime = System.currentTimeMillis();
            begin(type, message, args);
            if (profilingOutputEnabled) {
                profileOutput.append("[|");
            }
        }

        void endScope(Logger logger, String message, Object... args) {
            if (profilingOutputEnabled) {
                profileOutput.append("]|");
            }
            end(logger, message, args);
        }

        void drain() {
            while (!dataStack.isEmpty()) {
                end(null, "[missing end()]");
            }
        }

        private Thresholds getThreshold(String type) {
            Thresholds thresholds = defaultThresholdsByType.get(type);
            if (thresholds != null) {
                return thresholds;
            }
            return defaultThresholds;
        }
    }

    private static String format(String message, Object... args) {
        if (args != null && args.length > 0) {
            return String.format(message, args);
        }
        return message;
    }
}
