package com.junbo.identity.data.telesign.telesign

import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.langur.core.promise.Promise
import com.junbo.identity.data.telesign.telesign.verify.response.VerifyResponse
import groovy.transform.CompileStatic

/**
 * Created by minhao on 4/24/14.
 */
@CompileStatic
public interface TeleSign {
    Promise<VerifyResponse> verifyCode(UserTFA userTeleCode)
}