/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import groovy.transform.CompileStatic

import javax.ws.rs.core.*

/**
 * MockHttpHeaders.
 */
@CompileStatic
class MockHttpHeaders implements HttpHeaders {
    Long userId

    void setUserId(Long userId) {
        this.userId = userId
    }

    @Override
    List<String> getRequestHeader(String name) {
        return null
    }

    @Override
    String getHeaderString(String name) {
        return null
    }

    @Override
    MultivaluedMap<String, String> getRequestHeaders() {
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>()
        map.put('Authorization', ['Bearer ' + userId.toString()])
        return map
    }

    @Override
    List<MediaType> getAcceptableMediaTypes() {
        return null
    }

    @Override
    List<Locale> getAcceptableLanguages() {
        return null
    }

    @Override
    MediaType getMediaType() {
        return null
    }

    @Override
    Locale getLanguage() {
        return null
    }

    @Override
    Map<String, Cookie> getCookies() {
        return null
    }

    @Override
    Date getDate() {
        return null
    }

    @Override
    int getLength() {
        return 0
    }
}
