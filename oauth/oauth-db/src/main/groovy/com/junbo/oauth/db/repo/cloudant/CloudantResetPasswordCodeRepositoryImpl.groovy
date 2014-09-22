/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant

import com.junbo.configuration.topo.DataCenters
import com.junbo.oauth.db.repo.ResetPasswordCodeRepository
import com.junbo.oauth.spec.model.ResetPasswordCode
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils
/**
 * CloudantResetPasswordCodeRepositoryImpl.
 */
@CompileStatic
class CloudantResetPasswordCodeRepositoryImpl
        extends CloudantTokenRepositoryBase<ResetPasswordCode> implements ResetPasswordCodeRepository{

    @Override
    ResetPasswordCode get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null
        }

        return cloudantGetSyncWithFallback(code, tokenGenerator.hashKey(code))
    }

    @Override
    ResetPasswordCode getByHash(String hash, Integer dc) {
        if (StringUtils.isEmpty(hash)) {
            return null
        }
        ResetPasswordCode result = cloudantGetSync(hash)
        if (result == null) {
            if (dc == null) {
                return null
            }
            if (DataCenters.instance().isLocalDataCenter(dc)) {
                // already in the home dc of the token, no need to retry
                return null
            }
            def fallbackDbUri = getDbUriByDc(dc)
            if (fallbackDbUri == null) {
                return null
            }
            result = (ResetPasswordCode)getEffective().cloudantGet(fallbackDbUri, entityClass, hash).get()
        }
        return result
    }

    @Override
    void remove(String code) {
        cloudantDeleteSync(tokenGenerator.hashKey(code))
    }

    @Override
    void removeByHash(String hash) {
        cloudantDeleteSync(hash)
    }

    @Override
    void save(ResetPasswordCode resetPasswordCode) {
        if (resetPasswordCode.code == null) {
            resetPasswordCode.code = tokenGenerator.generateResetPasswordCode()
            resetPasswordCode.hashedCode = tokenGenerator.hashKey(resetPasswordCode.code)
            resetPasswordCode.dc = tokenGenerator.getTokenDc(resetPasswordCode.code)
        }

        cloudantPostSync(resetPasswordCode)
    }

    @Override
    void removeByUserIdEmail(Long userId, String email) {
        List<ResetPasswordCode> entities = queryViewSync('by_user_id_email', "$userId:$email")
        for (ResetPasswordCode code : entities) {
            cloudantDeleteSync(code)
        }
    }
}
