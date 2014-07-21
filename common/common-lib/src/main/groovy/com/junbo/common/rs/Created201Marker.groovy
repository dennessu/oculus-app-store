/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.rs

import com.junbo.common.id.Id
import com.junbo.common.util.IdFormatter
import com.junbo.langur.core.context.JunboHttpContext
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
abstract class Created201Marker {

    private Created201Marker() {
    }

    static void mark(Object resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException('resourceId is null')
        }

        // process response header
        if (JunboHttpContext.requestUri != null) {
            def location = JunboHttpContext.requestUri.toString()

            if (!location.endsWith('/')) {
                location += '/'
            }

            location += ((resourceId instanceof Id) ? IdFormatter.encodeId((Id) resourceId) : resourceId.toString())

            JunboHttpContext.responseStatus = 201
            JunboHttpContext.responseHeaders.add('Location', location)
        }
    }
}
