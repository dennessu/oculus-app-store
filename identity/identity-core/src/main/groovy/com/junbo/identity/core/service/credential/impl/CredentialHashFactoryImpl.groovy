package com.junbo.identity.core.service.credential.impl

import com.junbo.identity.core.service.credential.CredentialHash
import com.junbo.identity.core.service.credential.CredentialHashFactory
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/15/14.
 */
@CompileStatic
class CredentialHashFactoryImpl implements CredentialHashFactory {

    private final List<CredentialHash> credentialHashList

    CredentialHashFactoryImpl(List<CredentialHash> credentialHashList) {
        this.credentialHashList = credentialHashList
    }

    @Override
    List<CredentialHash> getAllCredentialHash() {
        return this.credentialHashList
    }
}
