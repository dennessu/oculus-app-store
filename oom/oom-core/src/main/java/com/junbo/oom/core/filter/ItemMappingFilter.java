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
public interface ItemMappingFilter {

    boolean skipItemMapping(ItemMappingEvent event, MappingContext context);

    void beginItemMapping(ItemMappingEvent event, MappingContext context);

    void endItemMapping(ItemMappingEvent event, MappingContext context);

}
