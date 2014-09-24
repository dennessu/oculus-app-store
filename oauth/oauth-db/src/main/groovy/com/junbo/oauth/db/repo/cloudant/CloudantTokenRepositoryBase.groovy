/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.util.UUIDUtils
import com.junbo.configuration.topo.DataCenters
import com.junbo.oauth.db.generator.TokenGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
/**
 * CloudantTokenRepositoryBase
 */
@CompileStatic
class CloudantTokenRepositoryBase<T> extends CloudantClient<T> {
    protected TokenGenerator tokenGenerator

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    T cloudantGetSyncWithFallback(String tokenWithDc, String tokenHash) {
        if (tokenHash == null) {
            tokenHash = tokenWithDc
        }

        T result = cloudantGetSync(tokenHash)
        if (result == null) {
            int tokenDc = tokenGenerator.getTokenDc(tokenWithDc)
            if (DataCenters.instance().isLocalDataCenter(tokenDc)) {
                // already in the home dc of the token, no need to retry
                return null
            }
            def fallbackDbUri = getDbUriByDc(tokenDc)
            if (fallbackDbUri == null) {
                return null
            }
            return (T)getEffective().cloudantGet(fallbackDbUri, entityClass, tokenHash).get()
        }
        return result
    }

    @Override
    T cloudantGetSyncUuidWithFallback(String uuidWithDc, String uuidHash) {
        if (uuidHash == null) {
            uuidHash = uuidWithDc
        }

        T result = cloudantGetSync(uuidHash)
        if (result == null) {
            int tokenDc = UUIDUtils.getDCFromUUID(uuidWithDc)
            if (DataCenters.instance().isLocalDataCenter(tokenDc)) {
                // already in the home dc of the token, no need to retry
                return null
            }
            def fallbackDbUri = getDbUriByDc(tokenDc)
            if (fallbackDbUri == null) {
                return null
            }
            return (T)getEffective().cloudantGet(fallbackDbUri, entityClass, uuidHash).get()
        }
        return result
    }
}
