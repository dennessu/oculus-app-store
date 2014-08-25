/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.TosId;
import com.junbo.store.spec.model.browse.document.Tos;

/**
 * The TocResponse class.
 */
public class TocResponse {

    private Tos tos;

    private String homeUrl;

    private String libraryUrl;

    public Tos getTos() {
        return tos;
    }

    public void setTos(Tos tos) {
        this.tos = tos;
    }

    public String getHomeUrl() {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public String getLibraryUrl() {
        return libraryUrl;
    }

    public void setLibraryUrl(String libraryUrl) {
        this.libraryUrl = libraryUrl;
    }
}
