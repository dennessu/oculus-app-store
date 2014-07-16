/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo

import com.junbo.oauth.spec.model.Consent
import groovy.transform.CompileStatic

/**
 * ConsentRepository.
 */
@CompileStatic
interface ConsentRepository {
    Consent saveConsent(Consent consent)

    Consent getConsent(Long userId, String clientId)

    Consent updateConsent(Consent consent, Consent oldConsent)

    void deleteConsent(Consent consent)
}
