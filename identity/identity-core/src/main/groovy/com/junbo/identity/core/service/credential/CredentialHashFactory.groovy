package com.junbo.identity.core.service.credential

import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/15/14.
 */
@CompileStatic
public interface CredentialHashFactory {
    List<CredentialHash> getAllCredentialHash()
}