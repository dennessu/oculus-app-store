package com.junbo.identity.clientproxy

import com.junbo.identity.spec.v1.model.UserTeleCode
import com.junbo.langur.core.promise.Promise
import com.telesign.verify.response.VerifyResponse
import groovy.transform.CompileStatic

/**
 * Created by minhao on 4/24/14.
 */
@CompileStatic
public interface TeleSign {
    Promise<VerifyResponse> verifyCode(UserTeleCode userTeleCode)
}