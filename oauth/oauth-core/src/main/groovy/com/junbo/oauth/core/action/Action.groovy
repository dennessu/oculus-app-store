/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import groovy.transform.CompileStatic

/**
 * Action.
 */
@CompileStatic
interface Action {
    boolean execute(ServiceContext context)
}
