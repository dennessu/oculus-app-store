package com.junbo.identity.core.service.credential

import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 2014/12/18.
 */
@CompileStatic
public interface CredentialHelper {
    Promise<Boolean> isValidPassword(UserId userId, String password)
}
