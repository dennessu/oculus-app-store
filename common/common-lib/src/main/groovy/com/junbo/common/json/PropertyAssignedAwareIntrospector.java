/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.json;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * Created by kg on 3/19/2014.
 */
public class PropertyAssignedAwareIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public Object findFilterId(Annotated a) {
        if (PropertyAssignedAware.class.isAssignableFrom(a.getRawType())) {
            return PropertyAssignedAwareFilter.class.getName();
        }

        return null;
    }
}
