/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.avalara

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * Created by LinYi on 14-3-10.
 */
@CompileStatic
class ResponseMessage {
    @JsonProperty('Summary')
    String summary
    @JsonProperty('Details')
    String details
    @JsonProperty('RefersTo')
    String refersTo
    @JsonProperty('Severity')
    SeverityLevel severity
    @JsonProperty('Source')
    String source
}
