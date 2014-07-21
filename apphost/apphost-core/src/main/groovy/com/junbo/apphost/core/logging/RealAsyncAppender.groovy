package com.junbo.apphost.core.logging

import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.spi.ILoggingEvent

/**
 * Created by contractor5 on 7/20/2014.
 */
class RealAsyncAppender extends AsyncAppender {

    RealAsyncAppender() {
        queueSize = 2048
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (remainingCapacity <= 0) {
            return;
        }

        super.append(eventObject)
    }
}
