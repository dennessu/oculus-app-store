/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.piid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.json.ObjectMapperProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * CascadeIdTest.
 */
public class CascadeIdTest {
    private ObjectMapper mapper = ObjectMapperProvider.instance();

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

    @Test
    public void testCollectionSupport() throws Exception {
        Person person = new Person();

        TestId id1 = new TestId();
        id1.setUserId(12345L);
        id1.setTestId(99999L);

        TestId id2 = new TestId();
        id2.setUserId(23456L);
        id2.setTestId(88888L);

        person.setTestIdList(Arrays.asList(id1, id2));

        String json = mapper.writeValueAsString(person);
        Person person2 = mapper.readValue(json, Person.class);

        Assert.assertEquals(person2.getTestIdList().size(), person.getTestIdList().size());
        Assert.assertEquals(person2.getTestIdList().get(0).getUserId(), person.getTestIdList().get(0).getUserId());
        Assert.assertEquals(person2.getTestIdList().get(0).getTestId(), person.getTestIdList().get(0).getTestId());

        Assert.assertEquals(person2.getTestIdList().get(1).getUserId(), person.getTestIdList().get(1).getUserId());
        Assert.assertEquals(person2.getTestIdList().get(1).getTestId(), person.getTestIdList().get(1).getTestId());
    }

    @Test
    public void testIncompleteSerialize() throws Exception {
        PaymentTransaction trx = new PaymentTransaction();

        TestId ti = new TestId();
        ti.setUserId(12345L);
        ti.setTestId(null);

        trx.setTestId(ti);

        String json = mapper.writeValueAsString(trx);
        PaymentTransaction trx2 = mapper.readValue(json, PaymentTransaction.class);

        Assert.assertEquals(trx2.getTestId().getUserId(), trx.getTestId().getUserId());
        Assert.assertNull(trx2.getTestId().getTestId());
    }

    @Test
    public void testNullDeserialize() throws Exception {
        String json = "{}";
        PaymentTransaction trx = mapper.readValue(json, PaymentTransaction.class);
        Assert.assertNull(trx.getTestId());
    }

    @Test
    public void testIncompleteDeserialize() throws Exception {
        String json = "{\"testId\":{\"href\":\"http://api.oculus.com/v1/users/000000003039/test-ids/\",\"id\":\"\"}}";

        PaymentTransaction trx = mapper.readValue(json, PaymentTransaction.class);
        Assert.assertNull(trx.getTestId().getTestId());
        Assert.assertNotNull(trx.getTestId().getUserId());
    }
}
