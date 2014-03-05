/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenGenerationService
import com.junbo.oauth.db.repo.AccessTokenRepository
import com.junbo.oauth.db.repo.RefreshTokenRepository
import com.junbo.oauth.spec.model.*
import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jose.util.Base64URL
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

import java.text.ParseException

/**
 * Javadoc.
 */
@CompileStatic
class TokenGenerationServiceImpl implements TokenGenerationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenGenerationServiceImpl)

    private static final String JWT_CONTENT_TYPE = 'JWT'
    private static final int LEFT_MOST_128_BITS = 16
    private static final Long MILLISECONDS_PER_SECOND = 1000L

    private Long defaultAccessTokenExpiration
    private Long defaultRefreshTokenExpiration
    private Long defaultIdTokenExpiration

    private AccessTokenRepository accessTokenRepository
    private RefreshTokenRepository refreshTokenRepository

    @Required
    void setDefaultAccessTokenExpiration(Long defaultAccessTokenExpiration) {
        this.defaultAccessTokenExpiration = defaultAccessTokenExpiration
    }

    @Required
    void setDefaultRefreshTokenExpiration(Long defaultRefreshTokenExpiration) {
        this.defaultRefreshTokenExpiration = defaultRefreshTokenExpiration
    }

    @Required
    void setDefaultIdTokenExpiration(Long defaultIdTokenExpiration) {
        this.defaultIdTokenExpiration = defaultIdTokenExpiration
    }

    @Required
    void setAccessTokenRepository(AccessTokenRepository accessTokenRepository) {
        this.accessTokenRepository = accessTokenRepository
    }

    @Required
    void setRefreshTokenRepository(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository
    }

    @Override
    AccessToken generateAccessToken(AppClient appClient, Long userId, Set<String> scopes) {
        Assert.notNull(appClient)
        Assert.notNull(appClient.clientId)
        Assert.notNull(userId)
        Assert.notNull(scopes)

        AccessToken accessToken = new AccessToken(
                clientId: appClient.clientId,
                userId: userId,
                scopes: scopes,
                expiredBy: new Date(System.currentTimeMillis() + defaultAccessTokenExpiration * MILLISECONDS_PER_SECOND)
        )

        accessTokenRepository.save(accessToken)

        return accessToken
    }

    @Override
    AccessToken getAccessToken(String tokenValue) {
        return accessTokenRepository.get(tokenValue)
    }

    @Override
    void removeAccessToken(String tokenValue) {
        accessTokenRepository.remove(tokenValue)
    }

    @Override
    RefreshToken generateRefreshToken(AppClient appClient, AccessToken accessToken, String salt) {
        Assert.notNull(appClient)
        Assert.notNull(accessToken)
        Assert.notNull(accessToken.tokenValue)

        RefreshToken refreshToken = new RefreshToken(
                clientId: appClient.clientId,
                userId: accessToken.userId,
                accessToken: accessToken,
                salt: salt,
                expiredBy: new Date(System.currentTimeMillis() +
                        defaultRefreshTokenExpiration * MILLISECONDS_PER_SECOND)
        )

        refreshTokenRepository.save(refreshToken)

        return refreshToken
    }

    @Override
    RefreshToken generateRefreshToken(AppClient appClient, AccessToken accessToken, RefreshToken oldRefreshToken) {
        Assert.notNull(appClient)
        Assert.notNull(accessToken)
        Assert.notNull(accessToken.tokenValue)

        RefreshToken refreshToken = new RefreshToken(
                tokenValue: oldRefreshToken.tokenValue,
                clientId: appClient.clientId,
                userId: accessToken.userId,
                accessToken: accessToken,
                salt: oldRefreshToken.salt,
                expiredBy: new Date(System.currentTimeMillis() +
                        defaultRefreshTokenExpiration * MILLISECONDS_PER_SECOND)
        )

        refreshTokenRepository.save(refreshToken)

        return refreshToken
    }

    @Override
    RefreshToken getAndRemoveRefreshToken(String tokenValue) {
        return refreshTokenRepository.getAndRemove(tokenValue)
    }

    @Override
    IdToken generateIdToken(AppClient client, String issuer, Long userId, String nonce, Date lastAuthDate,
                            AuthorizationCode code, AccessToken accessToken) {
        Assert.notNull(client)
        Assert.notNull(issuer)
        Assert.notNull(userId)

        IdToken idToken = new IdToken(
                iss: issuer,
                sub: userId.toString(),
                aud: client.clientId,
                exp: (Long) (new Date(System.currentTimeMillis() +
                        defaultIdTokenExpiration * MILLISECONDS_PER_SECOND).time / MILLISECONDS_PER_SECOND),
                iat: (Long) (new Date().time / MILLISECONDS_PER_SECOND),
                nonce: nonce,
        )

        if (lastAuthDate != null) {
            idToken.authTime = (Long) (lastAuthDate.time / MILLISECONDS_PER_SECOND)
        }

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256)
        header.contentType = JWT_CONTENT_TYPE

        JWSSigner signer = new MACSigner(client.clientSecret.bytes)

        if (code != null) {
            idToken.codeHash = generateLeftMostHash(signer.sign(header, code.code.bytes))
        }

        if (accessToken != null) {
            idToken.accessTokenHash = generateLeftMostHash(signer.sign(header, accessToken.tokenValue.bytes))
        }

        Payload payload = new Payload(JsonMarshaller.marshall(idToken))
        JWSObject jwsObject = new JWSObject(header, payload)
        jwsObject.sign(signer)

        idToken.tokenValue = jwsObject.serialize()

        return idToken
    }

    private static String generateLeftMostHash(Base64URL hash) {
        byte[] inputBytes = hash.decode()
        byte[] leftMostBytes = new byte[LEFT_MOST_128_BITS]
        for (int i = 0; i < inputBytes.length && i < LEFT_MOST_128_BITS; i++) {
            leftMostBytes[i] = inputBytes[i]
        }

        return Base64URL.encode(leftMostBytes).toString()
    }

    @Override
    IdToken parseIdToken(AppClient client, String tokenValue) {
        Assert.notNull(client)
        Assert.notNull(tokenValue)

        try {
            JWSVerifier verifier = new MACVerifier(client.clientSecret.bytes)
            JWSObject jwsObject = JWSObject.parse(tokenValue)

            if (jwsObject.verify(verifier)) {
                IdToken idToken = JsonMarshaller.unmarshall(IdToken, jwsObject.payload.toString())
                idToken.tokenValue = tokenValue
                return idToken
            } else {
                throw AppExceptions.INSTANCE.invalidIdToken().exception()
            }

        } catch (ParseException | JOSEException e) {
            LOGGER.error('Error parsing the id token', e)
            throw AppExceptions.INSTANCE.invalidIdToken().exception()
        }
    }
}
