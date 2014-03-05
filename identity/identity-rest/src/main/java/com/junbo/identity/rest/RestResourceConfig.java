/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
/**
 * Java cod for RestResourceConfig.
 */

@ApplicationPath("rest")
public class RestResourceConfig extends ResourceConfig {
    public RestResourceConfig() {
        packages(this.getClass().getPackage().getName());
    }
}
