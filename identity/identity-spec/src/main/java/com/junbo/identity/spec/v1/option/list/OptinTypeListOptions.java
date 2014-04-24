/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.option.list;

import javax.ws.rs.QueryParam;

/**
 * Created by liangfu on 4/17/14.
 */
public class OptinTypeListOptions {

    @QueryParam("value")
    private String value;

    @QueryParam("properties")
    private String properties;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }
}
