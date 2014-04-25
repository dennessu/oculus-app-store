package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserCommunicationId
import com.junbo.identity.spec.v1.model.UserCommunication
import com.junbo.identity.spec.v1.option.list.UserOptinListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
interface UserCommunicationValidator {
    Promise<UserCommunication> validateForGet(UserCommunicationId userOptinId)
    Promise<Void> validateForSearch(UserOptinListOptions options)
    Promise<Void> validateForCreate(UserCommunication userOptin)
    Promise<Void> validateForUpdate(UserCommunicationId userOptinId, UserCommunication userOptin, UserCommunication oldUserOptin)
}