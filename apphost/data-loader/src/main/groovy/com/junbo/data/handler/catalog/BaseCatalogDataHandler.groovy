/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.handler.catalog

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required

/**
 * BaseDataHandler.
 */
@CompileStatic
abstract class BaseCatalogDataHandler {
    protected final Logger logger = LoggerFactory.getLogger(getClass())
    protected boolean alwaysOverwrite

    @Required
    void setAlwaysOverwrite(boolean alwaysOverwrite) {
        this.alwaysOverwrite = alwaysOverwrite
    }

    void exit() {
        System.exit(0)
    }
}
