package com.junbo.email.clientproxy.mock

import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.UserId
import com.junbo.email.clientproxy.IdentityFacade
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by Wei on 4/9/14.
 */
@CompileStatic
class MockIdentityFacadeImpl implements IdentityFacade {

    Promise<String> getUserEmail(Long userId) {
        String email = 'csr@silkcloud.com'
        return Promise.pure(email)
    }
}