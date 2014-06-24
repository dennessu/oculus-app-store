/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson;

import com.junbo.common.cloudant.CloudantMarshaller;
import com.junbo.common.cloudant.PolymorphicCloudantMarshaller;
import com.junbo.common.id.UserId;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;


/**
 * JacksonCustomizationTest.
 */
public class CloudantMarshallerTest {
    private CloudantMarshaller polyMapper = PolymorphicCloudantMarshaller.instance();

    @Test
    public void testMap() throws Exception {
        // prepare data
        MyResourceMeta myResourceMeta = new MyResourceMeta();
        myResourceMeta.setId(new UserId(12345L));
        myResourceMeta.setUsername("hehe");

        Map<String, Object> states = new HashMap<>();

        states.put("test", myResourceMeta);
        states.put("test1", 123);

        // do serialization
        String json = polyMapper.marshall(states);

        Map<String, Object> state2 = polyMapper.unmarshall(json, HashMap.class, String.class, Object.class);
        String json2 = polyMapper.marshall(state2);

        assertEquals(json, json2);

        MyResourceMeta resourceMeta2 = (MyResourceMeta)state2.get("test");
        assertEquals(resourceMeta2.getId(), new UserId(12345L));
        assertEquals(resourceMeta2.getUsername(), "hehe");
    }
}
