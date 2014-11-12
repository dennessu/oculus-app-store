package com.junbo.identity.service

import com.junbo.common.id.TosId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTosAgreementId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserTosAgreement
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
public interface UserTosService {
    Promise<UserTosAgreement> get(UserTosAgreementId id)

    Promise<UserTosAgreement> create(UserTosAgreement model)

    Promise<UserTosAgreement> update(UserTosAgreement model, UserTosAgreement oldModel)

    Promise<Void> delete(UserTosAgreementId id)

    Promise<Results<UserTosAgreement>> searchByUserId(UserId userId, Integer limit, Integer offset)

    Promise<Results<UserTosAgreement>> searchByTosId(TosId tosId, Integer limit, Integer offset)

    Promise<Results<UserTosAgreement>> searchByUserIdAndTosId(UserId userId, TosId tosId, Integer limit, Integer offset)
}