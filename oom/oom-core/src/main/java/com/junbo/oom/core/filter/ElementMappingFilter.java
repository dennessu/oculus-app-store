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
public interface ElementMappingFilter {

    boolean skipElementMapping(ElementMappingEvent event, MappingContext context);

    void beginElementMapping(ElementMappingEvent event, MappingContext context);

    void endElementMapping(ElementMappingEvent event, MappingContext context);

}
