/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common;

import com.junbo.common.shuffle.Oculus48Id;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * IdEncodeTest.
 */
public class IdEncodeTest {
    @Test
    public void testEncodeId() {
        Long value = 400L;
        String encoded = Oculus48Id.encode(value);
        Assert.assertNotNull(encoded);
    }
}
