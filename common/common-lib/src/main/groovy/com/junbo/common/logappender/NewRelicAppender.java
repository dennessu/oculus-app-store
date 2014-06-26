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
import com.ning.http.util.DateUtil;
import org.springframework.util.StringUtils;

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
        // "(InProc/Remote) Method: create uri: " + __pathTemplate +" duration: " + (System.currentTimeMillis() - __startTime) + "ms " + __startDate.toString()
        String message = loggingEvent.getMessage();
        NewRelicEvent event = new NewRelicEvent();
        message = message.replaceFirst(PATTERN_INPROC_PREFIX, "");
        message = message.replaceFirst(PATTERN_REMOTE_PREFIX, "");
        message = message.trim();

        String methodName = extract(message, ' ');
        message = message.replaceFirst(methodName, "").trim();
        event.setMethodName(methodName);

        String uri = extract(message, ' ');
        message = message.replaceFirst(uri, "").trim();
        uri = extract(message, ' ');
        message = message.replaceFirst(uri, "").trim();
        event.setEventType(uri);

        String duration = extract(message, ' ');
        message = message.replaceFirst(duration, "").trim();
        duration = extract(message, ' ');
        message = message.replaceFirst(duration, "").trim();
        duration = duration.replaceAll("ms", "");
        event.setEventType(duration);

        try {
            event.setEventStartTime(DateUtil.parseDate(message));
        } catch (Exception e) {
            System.err.println("Exception while Parsing date: " + e.getMessage());
        }

        return event;
    }

    String extract(String original, char splitChar) {
        int endIndex = original.indexOf(splitChar);
        return original.substring(0, endIndex).trim();
    }
}
