package com.junbo.identity.core.service.validator

import com.junbo.common.id.TosId
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.option.list.TosListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/9/14.
 */
@CompileStatic
interface TosValidator {
    Promise<Tos> validateForGet(TosId tosId)
    Promise<Void> validateForSearch(TosListOptions options)
    Promise<Void> validateForCreate(Tos tos)
}