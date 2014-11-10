/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.handler
import com.junbo.langur.core.client.MessageTranscoder
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
/**
 * BaseDataHandler.
 */
@CompileStatic
abstract class BaseDataHandler implements DataHandler {
    protected final Logger logger = LoggerFactory.getLogger(getClass())
    protected MessageTranscoder transcoder


    @Required
    void setTranscoder(MessageTranscoder transcoder) {
        this.transcoder = transcoder
    }

    void exit() {
        System.exit(0)
    }
}
