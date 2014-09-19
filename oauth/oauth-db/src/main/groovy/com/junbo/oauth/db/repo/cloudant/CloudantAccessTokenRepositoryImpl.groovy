/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.client.CloudantDbUri
import com.junbo.common.cloudant.client.CloudantUri
import com.junbo.common.error.AppCommonErrors
import com.junbo.configuration.topo.DataCenters
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.AccessTokenRepository
import com.junbo.oauth.spec.model.AccessToken
import groovy.transform.CompileStatic
import org.apache.commons.codec.binary.Base64
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * CloudantAccessTokenRepositoryImpl.
 */
@CompileStatic
class CloudantAccessTokenRepositoryImpl extends CloudantClient<AccessToken> implements AccessTokenRepository {
    private static Logger logger = LoggerFactory.getLogger(AccessTokenRepository.class)
    private TokenGenerator tokenGenerator

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    AccessToken save(AccessToken accessToken) {
        if (accessToken.tokenValue == null) {
            accessToken.tokenValue = tokenGenerator.generateAccessToken(accessToken.userId)
            accessToken.hashedTokenValue = tokenGenerator.hashKey(accessToken.tokenValue)
        }

        return cloudantPostSync(accessToken)
    }

    @Override
    AccessToken get(String tokenValue) {
        if (StringUtils.isEmpty(tokenValue)) {
            return null
        }
        
        String tokenHash = tokenGenerator.hashKey(tokenValue)
        AccessToken token = cloudantGetSync(tokenHash)
        if (token == null) {
            Integer accessTokenDc = null
            try {
                accessTokenDc = getDcFromAccessToken(tokenValue)
            } catch (Exception e) {
                logger.error("Error occurred while parsing DC id from accessToken $tokenValue", e)
                return null
            }
            if (DataCenters.instance().currentDataCenterId() == accessTokenDc) {
                // already in the home dc of the access token, no need to retry
                return null
            }
            token = (AccessToken) getEffective().cloudantGet(getDbUri(accessTokenDc, tokenValue), entityClass, tokenHash).get()
        }
        if (token != null) {
            token.tokenValue = tokenValue
        }

        return token
    }

    private int getDcFromAccessToken(String accessToken) {
        byte[] originalBytes = Base64.decodeBase64(accessToken.replace("~", "_"))
        byte dcByte = originalBytes[tokenGenerator.accessTokenLength]
        return dcByte >> 4
    }

    private CloudantDbUri getDbUri(int dc, String accessToken) {
        CloudantUri uri
        try {
            uri = cloudantGlobalUri.getUri(dc)
        } catch (RuntimeException e) {
            logger.error("Cloudant URI not found for datacenter: $dc accessToken: $accessToken")
            throw AppCommonErrors.INSTANCE.invalidId("accessToken", accessToken).exception()
        }

        if (uri == null) {
            logger.error("Cloudant URI not found for datacenter: $dc accessToken: $accessToken")
            throw AppCommonErrors.INSTANCE.invalidId("accessToken", accessToken).exception()
        }
        return new CloudantDbUri(cloudantUri: uri, dbName: dbName, fullDbName: cloudantDbUri.fullDbName)
    }

    @Override
    List<AccessToken> findByRefreshToken(String refreshTokenValue) {
        return queryViewSync('by_refresh_token', refreshTokenValue)
    }

    @Override
    List<AccessToken> findByUserIdClientId(Long userId, String clientId) {
        return queryViewSync('by_user_id_client_id', "$userId:$clientId")
    }

    @Override
    AccessToken update(AccessToken accessToken, AccessToken oldAccessToken) {
        return cloudantPutSync(accessToken, oldAccessToken)
    }

    @Override
    void remove(String tokenValue) {
        if (StringUtils.hasText(tokenValue)) {
            cloudantDeleteSync(tokenGenerator.hashKey(tokenValue))
        }
    }

    @Override
    void removeByHash(String hash) {
        cloudantDeleteSync(hash)
    }

    @Override
    boolean isValidAccessToken(String tokenValue) {
        return tokenGenerator.isValidAccessToken(tokenValue)
    }
}
