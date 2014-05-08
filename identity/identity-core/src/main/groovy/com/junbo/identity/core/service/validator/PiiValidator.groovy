package com.junbo.identity.core.service.validator

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/29/14.
 */
@CompileStatic
interface PiiValidator {
    boolean handles(String type)
    Promise<Void> validateCreate(JsonNode value, UserId userId)
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue, UserId userId)
}
