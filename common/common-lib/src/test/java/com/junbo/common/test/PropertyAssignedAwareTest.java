/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.UserId;
import com.junbo.common.jackson.User;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.common.util.IdFormatter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Random;

/**
 * Java doc for IdTest.
 */
public class PropertyAssignedAwareTest {

    @Test
    public void testSerialize() throws Exception {

        ObjectMapper objectMapper = new ObjectMapperProvider().getContext(null);

        User user = new User();

        Assert.assertEquals("{}", objectMapper.writeValueAsString(user));

        user.setTest1(null);
        Assert.assertEquals("{\"test1\":null}", objectMapper.writeValueAsString(user));
    }

    @Test
    public void testDeserialize() throws Exception {

        ObjectMapper objectMapper = new ObjectMapperProvider().getContext(null);

        User user = objectMapper.readValue("{}", User.class);
        Assert.assertFalse(user.isPropertyAssigned("test1"));

        user = objectMapper.readValue("{\"test1\":null}", User.class);
        Assert.assertTrue(user.isPropertyAssigned("test1"));
    }
}
