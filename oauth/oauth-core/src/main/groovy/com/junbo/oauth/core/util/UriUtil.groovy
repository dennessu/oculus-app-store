/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.util

import groovy.transform.CompileStatic

import java.util.regex.Pattern

/**
 * UriUtil.
 */
@CompileStatic
class UriUtil {
    static boolean match(String uri, String uriTemplate) {
        if (uriTemplate.contains('*')) {
            String redirectUriPattern = uriTemplate.replace('.', '\\.').replace('?', '\\?')
                    .replace('*', '.*').concat('.*')
            if (Pattern.matches(redirectUriPattern, uri)) {
                return true
            }
        } else if (uri.startsWith(uriTemplate)) {
            return true
        }

        return false
    }
}
