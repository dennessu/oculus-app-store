/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.oauth.db.repo.ConsentRepository
import com.junbo.oauth.spec.model.Consent
import groovy.transform.CompileStatic

/**
 * CloudantConsentRepositoryImpl.
 */
@CompileStatic
class CloudantConsentRepositoryImpl extends CloudantClient<Consent> implements ConsentRepository {
    @Override
    Consent saveConsent(Consent consent) {
        return cloudantPostSync(consent)
    }

    @Override
    Consent getConsent(Long userId, String clientId) {
        return cloudantGetSync("$userId#$clientId")
    }

    @Override
    Consent updateConsent(Consent consent, Consent oldConsent) {
        return cloudantPutSync(consent, oldConsent)
    }

    @Override
    void deleteConsent(Consent consent) {
        cloudantDeleteSync(consent)
    }
}
