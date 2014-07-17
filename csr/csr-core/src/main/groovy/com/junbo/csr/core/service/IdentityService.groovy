package com.junbo.csr.core.service

import com.junbo.common.id.GroupId
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-7-17.
 */
@CompileStatic
public interface IdentityService {
    Promise<List<User>> getUserByUserEmail(String userEmail)
    Promise<User> getUserByUsername(String username)
    Promise<User> getUserByVerifiedEmail(String userEmail)
}