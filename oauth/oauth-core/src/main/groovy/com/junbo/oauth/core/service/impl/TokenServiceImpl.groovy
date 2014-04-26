/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.common.id.UserId
import com.junbo.common.util.IdFormatter
import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenService
import com.junbo.oauth.core.util.AuthorizationHeaderUtil
import com.junbo.oauth.core.util.UriUtil
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.AccessTokenRepository
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.db.repo.RefreshTokenRepository
import com.junbo.oauth.spec.model.*
import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jose.util.Base64URL
import groovy.transform.CompileStatic
import org.apache.commons.codec.binary.Hex
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

import java.security.MessageDigest
import java.text.ParseException

/**
 * Javadoc.
 */
@CompileStatic
class TokenServiceImpl implements TokenService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl)

    private static final String JWT_CONTENT_TYPE = 'JWT'
    private static final int LEFT_MOST_128_BITS = 16
    private static final Long MILLISECONDS_PER_SECOND = 1000L

    private Long defaultAccessTokenExpiration
    private Long defaultRefreshTokenExpiration
    private Long defaultIdTokenExpiration

    private AccessTokenRepository accessTokenRepository
    private RefreshTokenRepository refreshTokenRepository
    private ClientRepository clientRepository

    private TokenGenerator tokenGenerator

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

    @Required
    void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository
    }

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    AccessToken generateAccessToken(Client client, Long userId, Set<String> scopes) {
        Assert.notNull(client, 'client is null')
        Assert.notNull(client.clientId, 'client.clientId is null')
        Assert.notNull(userId, 'userId is null')
        Assert.notNull(scopes, 'scopes is null')

        AccessToken accessToken = new AccessToken(
                clientId: client.clientId,
                userId: userId,
                scopes: scopes,
                expiredBy: new Date(System.currentTimeMillis() + defaultAccessTokenExpiration * MILLISECONDS_PER_SECOND)
        )

        return accessTokenRepository.save(accessToken)
    }

    @Override
    AccessToken getAccessToken(String tokenValue) {
        return accessTokenRepository.get(tokenValue)
    }

    @Override
    AccessToken extractAccessToken(String authorization) {
        String tokenValue = AuthorizationHeaderUtil.extractAccessToken(authorization)

        AccessToken accessToken = accessTokenRepository.get(tokenValue)
        if (accessToken == null) {
            throw AppExceptions.INSTANCE.invalidAuthorization().exception()
        }

        if (accessToken.isExpired()) {
            throw AppExceptions.INSTANCE.expiredAccessToken().exception()
        }

        return accessToken
    }

    @Override
    List<AccessToken> getAccessTokenByUserIdClientId(Long userId, String clientId) {
        return accessTokenRepository.findByUserIdClientId(userId, clientId)
    }

    @Override
    AccessToken updateAccessToken(AccessToken accessToken) {
        return accessTokenRepository.update(accessToken)
    }

    @Override
    RefreshToken generateRefreshToken(Client client, AccessToken accessToken, String salt) {
        Assert.notNull(client, 'client is null')
        Assert.notNull(accessToken, 'accessToken is null')
        Assert.notNull(accessToken.tokenValue, 'accessToken.tokenValue is null')

        RefreshToken refreshToken = new RefreshToken(
                clientId: client.clientId,
                userId: accessToken.userId,
                accessToken: accessToken,
                salt: salt,
                expiredBy: new Date(System.currentTimeMillis() +
                        defaultRefreshTokenExpiration * MILLISECONDS_PER_SECOND)
        )

        return refreshTokenRepository.save(refreshToken)
    }

    @Override
    RefreshToken generateRefreshToken(Client client, AccessToken accessToken, RefreshToken oldRefreshToken) {
        Assert.notNull(client, 'client is null')
        Assert.notNull(accessToken, 'accessToken is null')
        Assert.notNull(accessToken.tokenValue, 'accessToken.tokenValue is null')

        RefreshToken refreshToken = new RefreshToken(
                tokenValue: oldRefreshToken.tokenValue,
                clientId: client.clientId,
                userId: accessToken.userId,
                accessToken: accessToken,
                salt: oldRefreshToken.salt,
                expiredBy: new Date(System.currentTimeMillis() +
                        defaultRefreshTokenExpiration * MILLISECONDS_PER_SECOND)
        )

        return refreshTokenRepository.save(refreshToken)
    }

    @Override
    List<RefreshToken> getRefreshTokenByUserIdClientId(Long userId, String clientId) {
        return refreshTokenRepository.findByUserIdClientId(userId, clientId)
    }

    @Override
    RefreshToken getAndRemoveRefreshToken(String tokenValue) {
        return refreshTokenRepository.getAndRemove(tokenValue)
    }

    @Override
    IdToken generateIdToken(Client client, String issuer, Long userId, String nonce, Date lastAuthDate,
                            AuthorizationCode code, AccessToken accessToken) {
        Assert.notNull(client, 'client is null')
        Assert.notNull(issuer, 'issuer is null')
        Assert.notNull(userId, 'userId is null')

        IdToken idToken = new IdToken(
                iss: issuer,
                sub: IdFormatter.encodeId(new UserId(userId)),
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
    IdToken parseIdToken(String tokenValue) {
        Assert.notNull(tokenValue)

        try {
            JWSObject jwsObject = JWSObject.parse(tokenValue)
            IdToken idToken = JsonMarshaller.unmarshall(jwsObject.payload.toString(), IdToken)

            def client = clientRepository.getClient(idToken.aud)

            if (client != null) {
                JWSVerifier verifier = new MACVerifier(client.clientSecret.bytes)
                if (!jwsObject.verify(verifier)) {
                    throw AppExceptions.INSTANCE.invalidIdToken().exception()
                }
            } else {
                throw AppExceptions.INSTANCE.invalidClientId(idToken.aud).exception()
            }

            idToken.tokenValue = tokenValue
            return idToken
        } catch (IOException | ParseException | JOSEException e) {
            LOGGER.error('Error parsing the id token', e)
            throw AppExceptions.INSTANCE.invalidIdToken().exception()
        }
    }

    @Override
    void revokeAccessToken(String tokenValue, Client client) {
        Assert.notNull(tokenValue, 'tokenValue is null')
        AccessToken accessToken = accessTokenRepository.get(tokenValue)

        if (accessToken != null) {
            if (accessToken.clientId != client.clientId) {
                throw AppExceptions.INSTANCE.tokenClientNotMatch().exception()
            }

            accessTokenRepository.remove(accessToken.tokenValue)

            if (accessToken.refreshTokenValue != null) {
                revokeRefreshToken(accessToken.refreshTokenValue, client)
            }
        }
    }

    @Override
    void revokeRefreshToken(String tokenValue, Client client) {
        Assert.notNull(tokenValue, 'tokenValue is null')
        RefreshToken refreshToken = refreshTokenRepository.get(tokenValue)

        if (refreshToken != null) {
            if (refreshToken.clientId != client.clientId) {
                throw AppExceptions.INSTANCE.tokenClientNotMatch().exception()
            }

            refreshTokenRepository.getAndRemove(tokenValue)
        }

        def accessTokens = accessTokenRepository.findByRefreshToken(tokenValue)
        accessTokens.each { AccessToken token ->
            accessTokenRepository.remove(token.tokenValue)
        }
    }

    @Override
    boolean isValidAccessToken(String tokenValue) {
        return accessTokenRepository.isValidAccessToken(tokenValue)
    }

    @Override
    boolean isValidRefreshToken(String tokenValue) {
        return refreshTokenRepository.isValidRefreshToken(tokenValue)
    }

    @Override
    String generateSessionState(String clientId, String redirectUri, String sessionId) {

        def origin = UriUtil.getOrigin(redirectUri)
        def salt = tokenGenerator.generateSalt()

        String rawSessionState = "$clientId $origin $sessionId $salt"

        MessageDigest digest = MessageDigest.getInstance("SHA-256")
        byte[] hash = digest.digest(rawSessionState.getBytes("UTF-8"))
        String sessionState = Hex.encodeHexString(hash) + ".$salt"

        return sessionState
    }
}
