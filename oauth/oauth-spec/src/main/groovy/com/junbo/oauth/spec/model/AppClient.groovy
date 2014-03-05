/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import groovy.transform.CompileStatic

/**
 * AppClient.
 */
@CompileStatic
class AppClient {
    String clientId
    String clientSecret
    Set<String> allowedRedirectUris
    String defaultRedirectUri
    Set<String> allowedScopes
    Set<String> defaultScopes
    Set<ResponseType> allowedResponseTypes
    String idTokenIssuer
}
