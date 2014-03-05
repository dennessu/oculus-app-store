/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.test;

import com.junbo.oom.processor.MapperTestBase;
import com.junbo.oom.processor.WithClasses;
import com.junbo.oom.processor.mapper.SimpleMapper;
import com.junbo.oom.processor.mapper.SimpleMapper2;
import org.testng.annotations.Test;

/**
 * Java doc.
 */
@WithClasses({
        SimpleMapper.class,
        SimpleMapper2.class
})
public class CompileTest extends MapperTestBase {

    @Test
    public void testCompile() {
    }
}
