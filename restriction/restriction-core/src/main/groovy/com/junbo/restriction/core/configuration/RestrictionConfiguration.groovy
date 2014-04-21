/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.core.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.junbo.restriction.spec.internal.Restriction
import org.springframework.core.io.Resource

/**
 * RestrictionConfiguration.
 */
class RestrictionConfiguration {

    private final Resource config

    private final List<Restriction> restrictions

    private static ObjectMapper mapper

    static {
        mapper = new ObjectMapper()
    }

    RestrictionConfiguration() { }

    RestrictionConfiguration(Resource config) {
        this.config = config
        if (restrictions == null) {
            def type = mapper.typeFactory.constructParametricType(List, Restriction)
            restrictions = mapper.readValue(config.file, type)
        }
    }

    List<Restriction> getRestrictions() {
        return restrictions
    }
}
