/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.logappender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.junbo.common.json.ObjectMapperProvider;

/**
 * Created by liangfu on 6/25/14.
 */
public class NewRelicAppenderBase extends UnsynchronizedAppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent eventObject) {
        try {
            NewRelicRequest tr = new NewRelicRequest("https://insights.newrelic.com/beta_api/accounts", "693278", "f70fd046c4ed11c84773b7ca415f824f3d5dfdb4");

            NewRelicEvent event = new NewRelicEvent();
            event.setApiDuration("50");
            event.setApiName("createUser");

            tr.setPostBody(ObjectMapperProvider.instance().writeValueAsString(event));
            tr.executeRequest();
        }
        catch (Exception e) {
            System.err.println("IOException while executing API: " + e.getMessage());
        }
    }
}
