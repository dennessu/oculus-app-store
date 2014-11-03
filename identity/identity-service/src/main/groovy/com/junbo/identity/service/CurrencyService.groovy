package com.junbo.identity.service

import com.junbo.common.enumid.CurrencyId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
public interface CurrencyService {

    Promise<com.junbo.identity.spec.v1.model.Currency> get(CurrencyId id);

    Promise<com.junbo.identity.spec.v1.model.Currency> create(com.junbo.identity.spec.v1.model.Currency model);

    Promise<com.junbo.identity.spec.v1.model.Currency> update(com.junbo.identity.spec.v1.model.Currency model,
                                                              com.junbo.identity.spec.v1.model.Currency oldModel);

    Promise<Void> delete(CurrencyId id);

    Promise<Results<com.junbo.identity.spec.v1.model.Currency>> searchAll(Integer limit, Integer offset)
}