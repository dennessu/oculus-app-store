/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.json;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * Feature used to register Jackson JSON providers.
 * This class is already defined in 'org.glassfish.jersey.media:jersey-media-json-jackson:2.5.1'
 * We rewrite is as jersey-media-json-jackson is still using 1.x jackson which will conflict
 * with our 2.x jackson
 *
 */
public class JacksonFeature implements Feature {

    @Override
    public boolean configure(final FeatureContext context) {
        context.register(JacksonJaxbJsonProvider.class, MessageBodyReader.class, MessageBodyWriter.class);
        return true;
    }
}
