/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */


package com.junbo.oauth.spec.model

import groovy.transform.CompileStatic

/**
 * TokenEndpointAuthMethod.
 */
@CompileStatic
enum TokenEndpointAuthMethod {
    NONE,
    CLIENT_SECRET_POST,
    CLIENT_SECRET_BASIC
}
