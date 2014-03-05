/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.test;

import com.junbo.langur.processor.MapperTestBase;
import com.junbo.langur.processor.WithClasses;
import org.testng.annotations.Test;
/**
 * Created by kevingu on 11/21/13.
 */
@WithClasses({
        CategoryResource.class
})
public class CompileTest extends MapperTestBase {

    @Test
    public void testCompile() {
    }
}
