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

            uri1.parseServerAuthority()

            return true
        } catch (IllegalArgumentException | URISyntaxException e) {
            LOGGER.debug('Invalid uri format', e)
            return false
        }
    }

    private static boolean authorityMatches(String authority, String authorityTemplate) {
        if (StringUtils.isEmpty(authority)) {
            return false
        }

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

    static Map<String, String> localeMap = new HashMap<String, String>()
    static {
        localeMap.put("ko-KR", "ko-KR");
        localeMap.put("it-IT", "it-IT");
        localeMap.put("it-SM", "it-IT");
        localeMap.put("it-VA", "it-IT");
        localeMap.put("fr-BE", "fr-FR");
        localeMap.put("fr-BF", "fr-FR");
        localeMap.put("fr-BL", "fr-FR");
        localeMap.put("fr-CA", "fr-FR");
        localeMap.put("fr-CF", "fr-FR");
        localeMap.put("fr-DJ", "fr-FR");
        localeMap.put("fr-FR", "fr-FR");
        localeMap.put("fr-GG", "fr-FR");
        localeMap.put("fr-GQ", "fr-FR");
        localeMap.put("fr-HT", "fr-FR");
        localeMap.put("fr-JE", "fr-FR");
        localeMap.put("fr-KM", "fr-FR");
        localeMap.put("fr-LU", "fr-FR");
        localeMap.put("fr-MC", "fr-FR");
        localeMap.put("fr-MF", "fr-FR");
        localeMap.put("fr-ML", "fr-FR");
        localeMap.put("fr-NC", "fr-FR");
        localeMap.put("fr-NE", "fr-FR");
        localeMap.put("fr-PF", "fr-FR");
        localeMap.put("fr-PM", "fr-FR");
        localeMap.put("fr-SC", "fr-FR");
        localeMap.put("fr-TD", "fr-FR");
        localeMap.put("fr-TF", "fr-FR");
        localeMap.put("fr-WF", "fr-FR");
        localeMap.put("fr-YT", "fr-FR");
        localeMap.put("de-AT", "de-DE");
        localeMap.put("de-BE", "de-DE");
        localeMap.put("de-CH", "de-DE");
        localeMap.put("de-DE", "de-DE");
        localeMap.put("de-LI", "de-DE");
        localeMap.put("de-LU", "de-DE");
        localeMap.put("es-ES", "es-ES");
        localeMap.put("es-GQ", "es-ES");
        localeMap.put("es-EH", "es-ES");
        localeMap.put("es-AR", "es-MX");
        localeMap.put("es-BO", "es-MX");
        localeMap.put("es-CL", "es-MX");
        localeMap.put("es-CO", "es-MX");
        localeMap.put("es-CR", "es-MX");
        localeMap.put("es-CU", "es-MX");
        localeMap.put("es-DO", "es-MX");
        localeMap.put("es-EC", "es-MX");
        localeMap.put("es-GT", "es-MX");
        localeMap.put("es-HN", "es-MX");
        localeMap.put("es-MX", "es-MX");
        localeMap.put("es-NI", "es-MX");
        localeMap.put("es-PA", "es-MX");
        localeMap.put("es-PE", "es-MX");
        localeMap.put("es-PR", "es-MX");
        localeMap.put("es-PY", "es-MX");
        localeMap.put("es-SV", "es-MX");
        localeMap.put("es-UY", "es-MX");
        localeMap.put("es-VE", "es-MX");
    }

    static String replaceLocale(String uri, String locale) {
        locale = locale.replaceFirst('_', '-')
        String convertedLocale;
        // mapped all known locale , for unknown locale , mapped to en-US,
        // This is a shore term solution for oculus launch on new region. We should find a permanent solution for this.
        if (localeMap.containsKey(locale)) {
            convertedLocale = localeMap.get(locale)
        }
        else {
            convertedLocale = "en-US"
        }

        return uri.replaceFirst('/locale', '/' + convertedLocale);
    }
}
