/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.entity

import com.junbo.oauth.spec.model.GrantType
import com.junbo.oauth.spec.model.ResponseType
import com.junbo.oauth.spec.model.TokenEndpointAuthMethod
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class ClientEntity extends BaseEntity {
    String clientSecret
    String clientName
    Long ownerUserId
    Set<String> redirectUris
    String defaultRedirectUri
    Set<String> scopes
    Set<String> defaultScopes
    Set<ResponseType> responseTypes
    Set<GrantType> grantTypes
    String idTokenIssuer
    Set<String> logoutRedirectUris
    String defaultLogoutRedirectUri
    TokenEndpointAuthMethod tokenEndpointAuthMethod
    String logoUri
    Set<String> contacts
}
