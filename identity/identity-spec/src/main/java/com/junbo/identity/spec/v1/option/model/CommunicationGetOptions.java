/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.option.model;

import javax.ws.rs.QueryParam;

/**
 * Created by xiali_000 on 4/21/2014.
 */
public class CommunicationGetOptions {

    @QueryParam("properties")
    private String properties;

    @QueryParam("locale")
    private String locale;

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
