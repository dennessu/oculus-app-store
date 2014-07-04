/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.authorization.AuthorizeContext
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.ClientService
import com.junbo.oauth.core.service.ScopeService
import com.junbo.oauth.core.service.OAuthTokenService
import com.junbo.oauth.core.util.UriUtil
import com.junbo.oauth.db.exception.DBUpdateConflictException
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.spec.model.Client
import com.junbo.oauth.spec.model.TokenInfo
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import java.util.regex.Pattern

/**
 * ClientServiceImpl.
 */
@CompileStatic
class ClientServiceImpl implements ClientService {
    private static final String CLIENT_REGISTER_SCOPE = 'client.register'
    private static final String CLIENT_INFO_SCOPE = 'client.info'

    private static final Pattern EMAIL_PATTERN = Pattern.compile('[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.' +
            '[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*@\n(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?')

    private ClientRepository clientRepository
    private OAuthTokenService tokenService
    private TokenGenerator tokenGenerator
    private ScopeService scopeService

    @Required
    void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository
    }

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Required
    void setScopeService(ScopeService scopeService) {
        this.scopeService = scopeService
    }

    @Override
    Client saveClient(Client client) {
        if (!AuthorizeContext.hasScopes(CLIENT_REGISTER_SCOPE)) {
            throw AppExceptions.INSTANCE.insufficientScope().exception()
        }

        validateClient(client)

        client.ownerUserId = AuthorizeContext.currentUserId.value
        String clientId = tokenGenerator.generateClientId()
        while (clientRepository.getClient(clientId) != null) {
            clientId = tokenGenerator.generateClientId()
        }

        client.clientId = clientId
        client.clientSecret = tokenGenerator.generateClientSecret()
        client.needConsent = true

        clientRepository.saveClient(client)
        return client
    }

    @Override
    Client getClient(String clientId) {
        if (!AuthorizeContext.hasScopes(CLIENT_REGISTER_SCOPE)) {
            throw AppExceptions.INSTANCE.insufficientScope().exception()
        }

        Client client = clientRepository.getClient(clientId)

        if (client == null) {
            throw AppExceptions.INSTANCE.notExistClient(clientId).exception()
        }

        if (client.ownerUserId != AuthorizeContext.currentUserId) {
            throw AppExceptions.INSTANCE.notClientOwner().exception()
        }

        return client
    }

    @Override
    Client getClientInfo(String clientId) {
        if (!AuthorizeContext.hasScopes(CLIENT_INFO_SCOPE)) {
            throw AppExceptions.INSTANCE.insufficientScope().exception()
        }

        Client client = clientRepository.getClient(clientId)

        if (client == null) {
            throw AppExceptions.INSTANCE.notExistClient(clientId).exception()
        }

        return new Client(
                clientId: client.clientId,
                clientName: client.clientName,
                scopes: client.scopes,
                logoUri: client.logoUri
        )
    }

    @Override
    Client updateClient(String clientId, Client client) {
        if (StringUtils.isEmpty(client.revision)) {
            throw AppExceptions.INSTANCE.missingRevision().exception()
        }

        Client existingClient = getClient(clientId)

        if (client.revision != existingClient.revision) {
            throw AppExceptions.INSTANCE.updateConflict().exception()
        }

        if (client.clientId == null || existingClient.clientId != client.clientId) {
            throw AppExceptions.INSTANCE.cantUpdateFields('client_id').exception()
        }

        if (client.clientSecret == null || existingClient.clientSecret != client.clientSecret) {
            throw AppExceptions.INSTANCE.cantUpdateFields('client_secret').exception()
        }

        if (client.ownerUserId == null || existingClient.ownerUserId != client.ownerUserId) {
            throw AppExceptions.INSTANCE.cantUpdateFields('owner_user_id').exception()
        }

        if (client.idTokenIssuer == null || existingClient.idTokenIssuer != client.idTokenIssuer) {
            throw AppExceptions.INSTANCE.cantUpdateFields('id_token_issuer').exception()
        }

        if (client.needConsent == null ||  existingClient.needConsent != client.needConsent) {
            throw AppExceptions.INSTANCE.cantUpdateFields('need_consent').exception()
        }

        validateClient(client)

        client.clientId = clientId
        client.clientSecret = existingClient.clientSecret
        client.ownerUserId = existingClient.ownerUserId
        client.idTokenIssuer = existingClient.idTokenIssuer
        client.needConsent = existingClient.needConsent
        client.revision = existingClient.revision

        try {
            return clientRepository.updateClient(client)
        } catch (DBUpdateConflictException e) {
            throw AppExceptions.INSTANCE.updateConflict().exception()
        }
    }

    @Override
    void deleteClient(String clientId) {
        Client client = getClient(clientId)

        clientRepository.deleteClient(client)
    }

    @Override
    Client resetSecret(String clientId) {
        Client client = getClient(clientId)

        client.clientSecret = tokenGenerator.generateClientSecret()

        clientRepository.updateClient(client)
        return client
    }

    private void validateClient(Client client) {
        validateScope(client)
        validateRedirectUri(client)
        validateLogoutRedirectUri(client)
        validateLogoUri(client)
        validateContacts(client)
    }

    private void validateScope(Client client) {
        Collection<String> invalidScopes = client.scopes.findAll {
            String scope -> scopeService.getScope(scope) == null
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
                if (!UriUtil.isValidUri(escapedUri)) {
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
                if (!UriUtil.isValidUri(escapedUri)) {
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
        if (client.logoUri != null && !UriUtil.isValidUri(client.logoUri)) {
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

}
