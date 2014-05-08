/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.clientproxy.google

import groovy.transform.CompileStatic

/**
 * GoogleAccount.
 */
@CompileStatic
class GoogleAccount {
    String id
    String displayName
    Name name

    static class Name {
        String familyName
        String givenName
    }
}
