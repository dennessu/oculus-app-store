/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.ClientService
import com.junbo.oauth.core.util.UriUtil
import com.junbo.oauth.db.exception.DBUpdateConflictException
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.AccessTokenRepository
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.Client
import com.junbo.oauth.spec.model.TokenType
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import java.util.regex.Pattern

/**
 * ClientServiceImpl.
 */
@CompileStatic
class ClientServiceImpl implements ClientService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClientServiceImpl)

    private static final String CLIENT_SCOPE = 'client.register'
    private static final Set<String> AVAILABLE_SCOPES = ['openid', 'identity', 'order', 'billing', 'catalog'].toSet()

    private static final Pattern EMAIL_PATTERN = Pattern.compile('[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.' +
            '[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*@\n(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?')

    private ClientRepository clientRepository
    private AccessTokenRepository accessTokenRepository
    private TokenGenerator tokenGenerator

    @Required
    void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository
    }

    @Required
    void setAccessTokenRepository(AccessTokenRepository accessTokenRepository) {
        this.accessTokenRepository = accessTokenRepository
    }

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    Client postClient(String authorization, Client client) {
        AccessToken accessToken = parseAccessToken(authorization)

        validateClient(client)

        client.ownerUserId = accessToken.userId
        String clientId = tokenGenerator.generateClientId()
        while (clientRepository.getClient(clientId) != null) {
            clientId = tokenGenerator.generateClientId()
        }

        client.clientId = clientId
        client.clientSecret = tokenGenerator.generateClientSecret()

        clientRepository.saveClient(client)
        return client
    }

    @Override
    Client getClient(String authorization, String clientId) {
        AccessToken accessToken = parseAccessToken(authorization)

        Client client = clientRepository.getClient(clientId)

        if (client == null) {
            accessTokenRepository.remove(accessToken.tokenValue)
            throw AppExceptions.INSTANCE.notExistClient(clientId).exception()
        }

        if (client.ownerUserId != accessToken.userId) {
            throw AppExceptions.INSTANCE.notClientOwner().exception()
        }

        return client
    }

    @Override
    Client updateClient(String authorization, String clientId, Client client) {
        Client existingClient = getClient(authorization, clientId)

        if (client.clientId != null && existingClient.clientId != client.clientId) {
            throw AppExceptions.INSTANCE.cantUpdateFields('client_id').exception()
        }

        if (client.clientSecret != null && existingClient.clientSecret != client.clientSecret) {
            throw AppExceptions.INSTANCE.cantUpdateFields('client_secret').exception()
        }

        if (client.ownerUserId != null && existingClient.ownerUserId != client.ownerUserId) {
            throw AppExceptions.INSTANCE.cantUpdateFields('owner_user_id').exception()
        }

        if (client.idTokenIssuer != null && existingClient.idTokenIssuer != client.idTokenIssuer) {
            throw AppExceptions.INSTANCE.cantUpdateFields('id_token_issuer').exception()
        }

        validateClient(client)

        client.clientId = clientId
        client.clientSecret = existingClient.clientSecret
        client.ownerUserId = existingClient.ownerUserId
        client.idTokenIssuer = existingClient.idTokenIssuer
        client.revision = existingClient.revision

        try {
            clientRepository.updateClient(client)
        } catch (DBUpdateConflictException e) {
            throw AppExceptions.INSTANCE.updateConflict().exception()
        }
        return client
    }

    @Override
    void deleteClient(String authorization, String clientId) {
        Client client = getClient(authorization, clientId)

        clientRepository.deleteClient(client)
    }

    @Override
    Client resetSecret(String authorization, String clientId) {
        Client client = getClient(authorization, clientId)

        client.clientSecret = tokenGenerator.generateClientSecret()

        clientRepository.updateClient(client)
        return client
    }

    private static void validateClient(Client client) {
        validateScope(client)
        validateRedirectUri(client)
        validateLogoutRedirectUri(client)
        validateLogoUri(client)
        validateContacts(client)
    }

    private static void validateScope(Client client) {
        Collection<String> invalidScopes = client.scopes.findAll {
            String scope -> !AVAILABLE_SCOPES.contains(scope)
        }

        if (invalidScopes != null && !invalidScopes.isEmpty()) {
            throw AppExceptions.INSTANCE.
                    invalidScope(StringUtils.collectionToCommaDelimitedString(invalidScopes)).exception()
        }

        if (client.defaultScopes != null) {
            invalidScopes = client.defaultScopes.findAll {
                String scope -> !client.scopes.contains(scope)
            }

            if (invalidScopes != null && !invalidScopes.isEmpty()) {
                throw AppExceptions.INSTANCE.
                        invalidDefaultScope(StringUtils.collectionToCommaDelimitedString(invalidScopes)).exception()
            }
        }
    }

    private static void validateRedirectUri(Client client) {
        if (client.redirectUris != null) {
            client.redirectUris.each { String uri ->
                String escapedUri = uri.replace('*', 'a')
                if (!isValidUri(escapedUri)) {
                    throw AppExceptions.INSTANCE.invalidRedirectUri(uri).exception()
                }
            }

            if (client.defaultRedirectUri != null) {
                if (client.defaultRedirectUri.contains('*')) {
                    throw AppExceptions.INSTANCE.invalidDefaultRedirectUri(client.defaultRedirectUri,
                            'the default redirect uri cannot include wildcard').exception()
                }

                boolean valid = client.redirectUris.any { String uri ->
                    UriUtil.match(client.defaultRedirectUri, uri)
                }

                if (!valid) {
                    throw AppExceptions.INSTANCE.invalidDefaultRedirectUri(client.defaultRedirectUri,
                            'the default redirect uri must match at least one redirect uri').exception()
                }
            }
        }
    }

    private static void validateLogoutRedirectUri(Client client) {
        if (client.logoutRedirectUris != null) {
            client.logoutRedirectUris.each { String uri ->
                String escapedUri = uri.replace('*', 'a')
                if (!isValidUri(escapedUri)) {
                    throw AppExceptions.INSTANCE.invalidLogoutRedirectUri(uri).exception()
                }
            }

            if (client.defaultLogoutRedirectUri != null) {
                if (client.defaultLogoutRedirectUri.contains('*')) {
                    throw AppExceptions.INSTANCE.invalidDefaultLogoutRedirectUri(client.defaultLogoutRedirectUri,
                            'the default logout redirect uri cannot include wildcard').exception()
                }

                boolean valid = client.logoutRedirectUris.any { String uri ->
                    UriUtil.match(client.defaultLogoutRedirectUri, uri)
                }

                if (!valid) {
                    throw AppExceptions.INSTANCE.invalidDefaultLogoutRedirectUri(client.defaultLogoutRedirectUri,
                            'the default logout redirect uri must match at least one logout redirect uri').exception()
                }
            }
        }
    }

    private static void validateLogoUri(Client client) {
        if (client.logoUri != null && !isValidUri(client.logoUri)) {
            throw AppExceptions.INSTANCE.invalidLogoUri(client.logoUri).exception()
        }
    }

    private static void validateContacts(Client client) {
        if (client.contacts != null) {
            client.contacts.each { String contact ->
                if (!EMAIL_PATTERN.matcher(contact)) {
                    throw AppExceptions.INSTANCE.invalidContacts(contact).exception()
                }
            }
        }
    }

    private static boolean isValidUri(String uri) {
        try {
            URI.create(uri)
        } catch (IllegalArgumentException e) {
            LOGGER.debug('Invalid uri format', e)
            return false
        }

        return true
    }

    private AccessToken parseAccessToken(String authorization) {
        String[] tokens = authorization.split(' ')
        if (tokens.length != 2 || !tokens[0].equalsIgnoreCase(TokenType.BEARER.name())) {
            throw AppExceptions.INSTANCE.invalidAuthorization().exception()
        }

        AccessToken accessToken = accessTokenRepository.get(tokens[1])
        if (accessToken == null) {
            throw AppExceptions.INSTANCE.invalidAuthorization().exception()
        }

        if (accessToken.isExpired()) {
            throw AppExceptions.INSTANCE.expiredAccessToken().exception()
        }

        if (!accessToken.scopes.contains(CLIENT_SCOPE)) {
            throw AppExceptions.INSTANCE.insufficientScope().exception()
        }
        return accessToken
    }
}
