package com.junbo.identity.core.service.credential

import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/15/14.
 */
@CompileStatic
interface CredentialHash {
    boolean handles(Integer version)
    String hash(String key)
    boolean matches(String valueToValidate, String hash)
}
