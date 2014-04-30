package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserTeleCode
import com.junbo.identity.spec.v1.option.list.UserTeleListOptions
import com.junbo.identity.spec.v1.option.model.UserTeleGetOptions
import com.junbo.identity.spec.v1.resource.UserTeleResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
@Transactional
class UserTeleResourceImpl implements UserTeleResource {


    @Override
    Promise<UserTeleCode> create(UserId userId, UserTeleCode userTeleCode) {
        return null
    }

    @Override
    Promise<UserTeleCode> get(UserId userId, UserTeleId userTeleId, UserTeleGetOptions getOptions) {
        return null
    }

    @Override
    Promise<UserTeleCode> patch(UserId userId, UserTeleId userTeleId, UserTeleCode userTeleCode) {
        return null
    }

    @Override
    Promise<UserTeleCode> put(UserId userId, UserTeleId userTeleId, UserTeleCode userTeleCode) {
        return null
    }

    @Override
    Promise<Void> delete(UserId userId, UserTeleId userTeleId) {
        return null
    }

    @Override
    Promise<Results<UserTeleCode>> list(UserId userId, UserTeleListOptions listOptions) {
        return null
    }
}
