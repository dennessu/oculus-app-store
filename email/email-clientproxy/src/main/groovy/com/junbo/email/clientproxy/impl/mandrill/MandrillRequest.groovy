/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.clientproxy.impl.mandrill

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * Request of Mandrill.
 */
@CompileStatic
class MandrillRequest {
    String key

    @JsonProperty('template_name')
    String templateName

    @JsonProperty('template_content')
    Map<String,String> templateContent

    Message message
}
