/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * Client.
 */
@CompileStatic
class Client {
    @JsonProperty('client_id')
    String clientId

    @JsonProperty('client_secret')
    String clientSecret

    @JsonProperty('client_name')
    String clientName

    @JsonProperty('owner_user_id')
    Long ownerUserId

    @JsonProperty('redirect_uris')
    Set<String> redirectUris

    @JsonProperty('default_redirect_uri')
    String defaultRedirectUri

    Set<String> scopes

    @JsonProperty('default_scopes')
    Set<String> defaultScopes

    @JsonProperty('response_types')
    Set<ResponseType> responseTypes

    @JsonProperty('grant_types')
    Set<GrantType> grantTypes

    @JsonProperty('id_token_issuer')
    String idTokenIssuer

    @JsonProperty('logout_redirect_uris')
    Set<String> logoutRedirectUris

    @JsonProperty('default_logout_redirect_uri')
    String defaultLogoutRedirectUri

    @JsonProperty('token_endpoint_auth_method')
    TokenEndpointAuthMethod tokenEndpointAuthMethod

    @JsonProperty('logo_uri')
    String logoUri

    Set<String> contacts

    @JsonIgnore
    String revision
}
