package com.junbo.identity.service

import com.junbo.common.enumid.LocaleId
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
interface LocaleService {
    Promise<com.junbo.identity.spec.v1.model.Locale> get(LocaleId id)

    Promise<com.junbo.identity.spec.v1.model.Locale> create(com.junbo.identity.spec.v1.model.Locale model)

    Promise<com.junbo.identity.spec.v1.model.Locale> update(com.junbo.identity.spec.v1.model.Locale model,
                                                            com.junbo.identity.spec.v1.model.Locale oldModel)

    Promise<Void> delete(LocaleId id)

    Promise<List<com.junbo.identity.spec.v1.model.Locale>> searchAll(Integer limit, Integer offset)
}
