package com.junbo.identity.core.service.validator

import com.fasterxml.jackson.databind.JsonNode
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/29/14.
 */
@CompileStatic
interface PiiValidator {
    boolean handles(String type)
    void validate(JsonNode value)
}
