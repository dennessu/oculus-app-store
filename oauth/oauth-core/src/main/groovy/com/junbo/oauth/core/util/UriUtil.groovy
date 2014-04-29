/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.util

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.regex.Pattern

/**
 * UriUtil.
 */
@CompileStatic
class UriUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(UriUtil)

    static boolean match(String uri, String uriTemplate) {

        // todo: this doesn't work. more strict validation needed.
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

    static boolean isValidUri(String uri) {
        try {
            URI.create(uri)
        } catch (IllegalArgumentException e) {
            LOGGER.debug('Invalid uri format', e)
            return false
        }

        return true
    }

    static String getOrigin(String uriStr) {
        URI uri = URI.create(uriStr)

        def port = uri.port
        if ('https' == uri.scheme && 433 == port) {
            port = -1
        } else if ('http' == uri.scheme && 80 == port) {
            port = -1
        }

        if (port < 0) {
            "${uri.scheme}://${uri.host}"
        }

        return "${uri.scheme}://${uri.host}:$port"
    }
}
