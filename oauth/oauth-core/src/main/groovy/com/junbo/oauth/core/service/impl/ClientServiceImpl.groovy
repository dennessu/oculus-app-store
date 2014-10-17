/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl
import com.junbo.authorization.AuthorizeContext
import com.junbo.common.error.AppCommonErrors
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.core.service.ClientService
import com.junbo.oauth.core.service.OAuthTokenService
import com.junbo.oauth.core.service.ScopeService
import com.junbo.oauth.core.util.UriUtil
import com.junbo.oauth.db.exception.DBUpdateConflictException
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.spec.model.Client
import com.junbo.oauth.spec.option.PageableGetOptions
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import java.util.regex.Pattern
/**
 * ClientServiceImpl.
 */
@CompileStatic
class ClientServiceImpl implements ClientService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile('[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.' +
            '[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?')

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
    List<Client> getAllClients(PageableGetOptions options) {
        return clientRepository.getAllClients(options)
    }

    @Override
    Client saveClient(Client client) {
        validateClient(client)

        client.ownerUserId = AuthorizeContext.currentUserId
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
        Client client = clientRepository.getClient(clientId)

        if (client == null) {
            throw AppErrors.INSTANCE.notExistClient(clientId).exception()
        }

        return client
    }

    @Override
    Client getClientInfo(String clientId) {
        Client client = clientRepository.getClient(clientId)

        if (client == null) {
            throw AppErrors.INSTANCE.notExistClient(clientId).exception()
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
        if (StringUtils.isEmpty(client.rev)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('revision').exception()
        }

        Client existingClient = getClient(clientId)

        if (client.rev != existingClient.rev) {
            throw AppErrors.INSTANCE.updateConflict().exception()
        }

        if (client.clientId == null || existingClient.clientId != client.clientId) {
            throw AppErrors.INSTANCE.cantUpdateFields('client_id').exception()
        }

        if (client.clientSecret == null || existingClient.clientSecret != client.clientSecret) {
            throw AppErrors.INSTANCE.cantUpdateFields('client_secret').exception()
        }

        if (client.idTokenIssuer == null || existingClient.idTokenIssuer != client.idTokenIssuer) {
            throw AppErrors.INSTANCE.cantUpdateFields('id_token_issuer').exception()
        }

        if (client.needConsent == null || existingClient.needConsent != client.needConsent) {
            throw AppErrors.INSTANCE.cantUpdateFields('need_consent').exception()
        }

        validateClient(client)

        client.clientId = clientId
        client.clientSecret = existingClient.clientSecret
        client.ownerUserId = existingClient.ownerUserId
        client.idTokenIssuer = existingClient.idTokenIssuer
        client.needConsent = existingClient.needConsent
        client.rev = existingClient.rev

        try {
            return clientRepository.updateClient(client, existingClient)
        } catch (DBUpdateConflictException e) {
            throw AppErrors.INSTANCE.updateConflict().exception()
        }
    }

    @Override
    void deleteClient(String clientId) {
        Client client = getClient(clientId)

        if (client.ownerUserId != AuthorizeContext.currentUserId) {
            throw AppErrors.INSTANCE.notClientOwner().exception()
        }

        clientRepository.deleteClient(client)
    }

    @Override
    Client resetSecret(String clientId) {
        Client client = getClient(clientId)

        if (client.ownerUserId != AuthorizeContext.currentUserId) {
            throw AppErrors.INSTANCE.notClientOwner().exception()
        }

        client.clientSecret = tokenGenerator.generateClientSecret()
        clientRepository.updateClient(client, client)
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
            throw AppCommonErrors.INSTANCE.fieldInvalid('scope',
                    StringUtils.collectionToCommaDelimitedString(invalidScopes)).exception()
        }

        if (client.defaultScopes != null) {
            invalidScopes = client.defaultScopes.findAll {
                String scope -> !client.scopes.contains(scope)
            }

            if (invalidScopes != null && !invalidScopes.isEmpty()) {
                throw AppErrors.INSTANCE.
                        invalidDefaultScope(StringUtils.collectionToCommaDelimitedString(invalidScopes)).exception()
            }
        }
    }

    private static void validateRedirectUri(Client client) {
        if (client.redirectUris != null) {
            client.redirectUris.each { String uri ->
                if (!UriUtil.isValidRedirectUri(uri)) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('redirect_uri', uri).exception()
                }
            }

            if (client.defaultRedirectUri != null) {
                if (client.defaultRedirectUri.contains('*')) {
                    throw AppErrors.INSTANCE.invalidDefaultRedirectUri(client.defaultRedirectUri,
                            'the default redirect uri cannot include wildcard').exception()
                }

                boolean valid = client.redirectUris.any { String uri ->
                    UriUtil.match(client.defaultRedirectUri, uri)
                }

                if (!valid) {
                    throw AppErrors.INSTANCE.invalidDefaultRedirectUri(client.defaultRedirectUri,
                            'the default redirect uri must match at least one redirect uri').exception()
                }
            }
        }
    }

    private static void validateLogoutRedirectUri(Client client) {
        if (client.logoutRedirectUris != null) {
            client.logoutRedirectUris.each { String uri ->
                if (!UriUtil.isValidRedirectUri(uri)) {
                    throw AppErrors.INSTANCE.invalidLogoutRedirectUri(uri).exception()
                }
            }

            if (client.defaultLogoutRedirectUri != null) {
                if (client.defaultLogoutRedirectUri.contains('*')) {
                    throw AppErrors.INSTANCE.invalidDefaultLogoutRedirectUri(client.defaultLogoutRedirectUri,
                            'the default logout redirect uri cannot include wildcard').exception()
                }

                boolean valid = client.logoutRedirectUris.any { String uri ->
                    UriUtil.match(client.defaultLogoutRedirectUri, uri)
                }

                if (!valid) {
                    throw AppErrors.INSTANCE.invalidDefaultLogoutRedirectUri(client.defaultLogoutRedirectUri,
                            'the default logout redirect uri must match at least one logout redirect uri').exception()
                }
            }
        }
    }

    private static void validateLogoUri(Client client) {
        if (client.logoUri != null && !UriUtil.isValidUri(client.logoUri)) {
            throw AppErrors.INSTANCE.invalidLogoUri(client.logoUri).exception()
        }
    }

    private static void validateContacts(Client client) {
        if (client.contacts != null) {
            client.contacts.each { String contact ->
                if (!EMAIL_PATTERN.matcher(contact)) {
                    throw AppErrors.INSTANCE.invalidContacts(contact).exception()
                }
            }
        }
    }

}
