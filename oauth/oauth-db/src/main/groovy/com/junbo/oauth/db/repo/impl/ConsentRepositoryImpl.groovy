/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.impl

import com.junbo.oauth.db.dao.ConsentDAO
import com.junbo.oauth.db.entity.ConsentEntity
import com.junbo.oauth.db.repo.ConsentRepository
import com.junbo.oauth.spec.model.Consent
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * ConsentRepositoryImpl.
 */
@CompileStatic
class ConsentRepositoryImpl implements ConsentRepository {
    private static final String DELIMIER = '#'
    private ConsentDAO consentDAO

    @Required
    void setConsentDAO(ConsentDAO consentDAO) {
        this.consentDAO = consentDAO
    }

    @Override
    Consent saveConsent(Consent consent) {
        return wrap(consentDAO.save(unwrap(consent)))
    }

    @Override
    Consent getConsent(Long userId, String clientId) {
        return wrap(consentDAO.get(getId(userId, clientId)))
    }

    @Override
    Consent updateConsent(Consent consent, Consent oldConsent) {
        return wrap(consentDAO.update(unwrap(consent)))
    }

    @Override
    void deleteConsent(Consent consent) {
        if (consent != null) {
            consentDAO.delete(unwrap(consent))
        }
    }

    private static ConsentEntity unwrap(Consent consent) {
        if (consent == null) {
            return null
        }

        return new ConsentEntity(
                id: getId(consent.userId, consent.clientId),
                userId: consent.userId,
                clientId: consent.clientId,
                scopes: consent.scopes,
                revision: consent.rev
        )
    }

    private static Consent wrap(ConsentEntity entity) {
        if (entity == null) {
            return null
        }

        String[] tokens = entity.id.split(DELIMIER)

        return new Consent(
                userId: Long.parseLong(tokens[0]),
                clientId: tokens[1],
                scopes: entity.scopes,
                rev: entity.revision
        )
    }

    private static String getId(Long userId, String clientId) {
        return userId.toString() + DELIMIER + clientId
    }
}
