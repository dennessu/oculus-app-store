/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.core.filter;

import com.junbo.oom.core.MappingContext;

/**
 * Java doc.
 */
public class SkipNullPropertyFilter implements PropertyMappingFilter {

    @Override
    public boolean skipPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        return event.getSourceProperty() == null;
    }

    @Override
    public void beginPropertyMapping(PropertyMappingEvent event, MappingContext context) {
    }

    @Override
    public void endPropertyMapping(PropertyMappingEvent event, MappingContext context) {
    }
}
