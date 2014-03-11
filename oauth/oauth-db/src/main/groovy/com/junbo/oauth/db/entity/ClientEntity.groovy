/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.entity

import com.junbo.oauth.spec.model.ResponseType
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class ClientEntity extends BaseEntity {
    String clientId
    String clientSecret
    Set<String> allowedRedirectUris
    String defaultRedirectUri
    Set<String> allowedScopes
    Set<String> defaultScopes
    Set<ResponseType> allowedResponseTypes
    String idTokenIssuer
    Set<String> allowedLogoutRedirectUris
    String defaultLogoutRedirectUri
}
