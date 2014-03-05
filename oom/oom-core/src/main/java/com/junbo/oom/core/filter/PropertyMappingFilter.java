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
public interface PropertyMappingFilter {

    boolean skipPropertyMapping(PropertyMappingEvent event, MappingContext context);

    void beginPropertyMapping(PropertyMappingEvent event, MappingContext context);

    void endPropertyMapping(PropertyMappingEvent event, MappingContext context);

}
