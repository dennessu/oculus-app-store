/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.handler

import groovy.transform.CompileStatic

/**
 * DataHandler.
 */
@CompileStatic
interface DataHandler {
    void handle(String content)
}
