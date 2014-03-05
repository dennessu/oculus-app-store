/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
@GeneratePrisms({
        @GeneratePrism(value = Mapper.class, publicAccess = true),
        @GeneratePrism(value = Mappings.class, publicAccess = true)
})

package com.junbo.oom.processor;

/**
 * Java doc
 */

import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.Mappings;
import net.java.dev.hickory.prism.GeneratePrism;
import net.java.dev.hickory.prism.GeneratePrisms;
