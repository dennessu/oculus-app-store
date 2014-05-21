/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author dw
 */
public class Validator {

    private Validator() {

    }

    public static void Validate(String message, Object expect, Object actual) throws Exception {
        assertEquals(message, JsonHelper.ObjectToJsonNode(expect), JsonHelper.ObjectToJsonNode(actual));
    }
}
