/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.configuration;

import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * This config resource provide an restful resource to represent final properties.
 */
@Path("properties")
@Produces({MediaType.APPLICATION_JSON})
public class ConfigResource {
    @Autowired
    private ConfigService configService;

    @GET
    public SortedMap getAllConfigItems() {
        return new TreeMap(configService.getAllConfigItemsMasked());
    }
}


