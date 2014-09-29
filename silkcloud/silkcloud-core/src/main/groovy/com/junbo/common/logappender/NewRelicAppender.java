/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.logappender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.configuration.ConfigServiceManager;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by liangfu on 6/25/14.
 */
public class NewRelicAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private static final String PATTERN_REMOTE_PREFIX = "(Remote) Method: ";
    private static final String PATTERN_INPROC_PREFIX = "(InProc) Method: ";

    @Override
    protected void append(ILoggingEvent eventObject) {
        try {
            if (!checkLoggerFormatter(eventObject)) {
                return;
            }

            String accountId = ConfigServiceManager.instance().getConfigValue("common.newrelic.insights.account");
            String accountKey = ConfigServiceManager.instance().getConfigValue("common.newrelic.insights.key");
            NewRelicRequest tr = new NewRelicRequest("https://insights.newrelic.com/beta_api/accounts", accountId, accountKey);

            tr.setPostBody(ObjectMapperProvider.instance().writeValueAsString(build(eventObject)));
            tr.executeRequest();
        }
        catch (Exception e) {
            System.err.println("IOException while executing API: " + e.getMessage());
        }
    }

    private boolean checkLoggerFormatter(ILoggingEvent loggingEvent) {
        Boolean enableLogging = Boolean.parseBoolean(ConfigServiceManager.instance().getConfigValue("common.newrelic.insights.log.enable"));
        if (!enableLogging) {
            return false;
        }

        if (!StringUtils.isEmpty(loggingEvent.getMessage()) &&
             (loggingEvent.getMessage().startsWith(PATTERN_INPROC_PREFIX) || loggingEvent.getMessage().startsWith(PATTERN_REMOTE_PREFIX))) {
            return true;
        }

        return false;
    }

    private NewRelicEvent build(ILoggingEvent loggingEvent) {
        // The log format should be
        // "(Remote) Method: ${methodName} base: " + __target + " uri: ${path} duration: " + (System.currentTimeMillis() - __startTime) + "ms "
        // + __startDate.toString() + " machineName: " + __machineName
        String message = loggingEvent.getMessage();
        NewRelicEvent event = new NewRelicEvent();
        message = message.replace(PATTERN_INPROC_PREFIX, "");
        message = message.replace(PATTERN_REMOTE_PREFIX, "");
        message = message.trim();

        String methodName = extract(message, ' ');
        message = replaceFirstOccur(message, methodName, "").trim();
        event.setMethodName(methodName);

        String base = extract(message, ' ');
        message = replaceFirstOccur(message, base, "").trim();
        int endIndex = message.indexOf("uri:");
        base = message.substring(0, endIndex).trim();
        event.setBase(base);
        message = replaceFirstOccur(message, base, "").trim();

        endIndex = message.indexOf("duration:");
        String uri = message.substring(0, endIndex).trim();
        message = replaceFirstOccur(message, uri, "").trim();
        uri = replaceFirstOccur(uri, "uri:", "").trim();
        event.setEventType(formatEventType(uri));

        String duration = extract(message, ' ');
        message = replaceFirstOccur(message, duration, "").trim();
        duration = extract(message, ' ');
        message = replaceFirstOccur(message, duration, "").trim();
        duration = duration.replace("ms", "");
        event.setDuration(duration);

        endIndex = message.indexOf("machineName:");
        String dataStr = message.substring(0, endIndex).trim();
        message = replaceFirstOccur(message, dataStr, "").trim();

        try {
            event.setEventStartTime(new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH).parse(dataStr));
        } catch (Exception e) {
            System.err.println("Exception while Parsing date: " + e.getMessage());
        }

        message = replaceFirstOccur(message, "machineName:", "").trim();
        event.setMachineName(message);

        return event;
    }

    String replaceFirstOccur(String inString, String oldPattern, String newPattern) {
        if (StringUtils.isEmpty(inString) || StringUtils.isEmpty(oldPattern)) {
            return inString;
        }

        StringBuilder sb = new StringBuilder();
        int pos = 0; // our position in the old string
        int index = inString.indexOf(oldPattern);
        // the index of an occurrence we've found, or -1
        int patLen = oldPattern.length();
        sb.append(inString.substring(pos, index));
        sb.append(newPattern);
        pos = index + patLen;
        sb.append(inString.substring(pos));
        // remember to append any characters to the right of a match
        return sb.toString();
    }

    String extract(String original, char splitChar) {
        int endIndex = original.indexOf(splitChar);
        return original.substring(0, endIndex).trim();
    }

    String formatEventType(String value) {
        value = value.replace("/", "_");
        value = value.replace(" ", "_");
        value = value.replace(",", "_");
        value = value.replace(".", "_");

        return value;
    }
}
