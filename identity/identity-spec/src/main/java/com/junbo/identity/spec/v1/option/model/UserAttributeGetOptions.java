/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.option.model;

import javax.ws.rs.QueryParam;

/**
 * Created by xiali_000 on 2014/12/19.
 */
public class UserAttributeGetOptions {
    @QueryParam("properties")
    private String properties;

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }
}
