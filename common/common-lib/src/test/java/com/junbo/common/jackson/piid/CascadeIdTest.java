/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.piid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.jackson.PaymentInstrument;
import com.junbo.common.jackson.deserializer.ResourceAwareDeserializationContext;
import com.junbo.common.jackson.serializer.ResourceAwareSerializerProvider;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CascadeIdTest.
 */
public class CascadeIdTest {
    private ObjectMapper mapper =
            new ObjectMapper(null, new ResourceAwareSerializerProvider(), new ResourceAwareDeserializationContext());

    @BeforeClass
    public void setUp() {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Test
    public void testBVT() throws Exception {
        PaymentTransaction trx = new PaymentTransaction();

        TestId ti = new TestId();
        ti.setUserId(12345L);
        ti.setTestId(99999L);

        trx.setTestId(ti);

        String json = mapper.writeValueAsString(trx);
        PaymentTransaction trx2 = mapper.readValue(json, PaymentTransaction.class);

        Assert.assertEquals(trx2.getTestId().getUserId(), trx.getTestId().getUserId());
        Assert.assertEquals(trx2.getTestId().getTestId(), trx.getTestId().getTestId());
    }
}
