/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.util

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.StringUtils

import java.util.regex.Pattern


/**
 * UriUtil.
 */
@CompileStatic
class UriUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(UriUtil)

    static boolean match(String uri, String uriTemplate) {
        try {
            URI uri1 = URI.create(uri)
            URI allowed = URI.create(uriTemplate)
            if (!uriPartMatches(uri1.scheme, allowed.scheme)) {
                return false
            }

            if (!authorityMatches(uri1.authority, allowed.authority)) {
                return false
            }

            if (!uriPartMatches(uri1.path, allowed.path)) {
                return false
            }

            if (StringUtils.hasText(uri1.fragment)) {
                return false
            }

            return true
        } catch (IllegalArgumentException e) {
            LOGGER.debug('Invalid uri format', e)
            return false
        }
    }

    private static boolean authorityMatches(String authority, String authorityTemplate) {
        if (StringUtils.isEmpty(authorityTemplate)) {
            return true
        }

        if (authorityTemplate.contains('*')) {
            String redirectUriPattern =
                    '^' + authorityTemplate.replace('.', '\\.').replace('?', '\\?').replace('*', '[^@:#?/%]*') + '$'

            return Pattern.matches(redirectUriPattern, authority)
        }

        return authority == authorityTemplate
    }

    private static boolean uriPartMatches(String part, String partTemplate) {
        if (StringUtils.isEmpty(partTemplate)) {
            return true
        }

        if (partTemplate.contains('*')) {
            String redirectUriPattern =
                    '^' + partTemplate.replace('.', '\\.').replace('?', '\\?').replace('*', '.*') + '$'

            return Pattern.matches(redirectUriPattern, part)
        }

        return part == partTemplate
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

    static boolean isValidRedirectUri(String uri) {
        try {
            URI uri1 = URI.create(uri)
            if (StringUtils.hasText(uri1.fragment)) {
                return false
            }
            return true
        } catch (IllegalArgumentException e) {
            LOGGER.debug('Invalid uri format', e)
            return false
        }
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
