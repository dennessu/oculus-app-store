/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.jackson.deserializer.ResourceAwareDeserializationContext;
import com.junbo.common.jackson.serializer.ResourceAwareSerializerProvider;
import com.junbo.common.shuffle.Oculus48Id;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JacksonCustomizationTest.
 */
public class JacksonCustomizationTest {
    private ObjectMapper mapper =
            new ObjectMapper(null, new ResourceAwareSerializerProvider(), new ResourceAwareDeserializationContext());

    @BeforeClass
    public void setUp() {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Test
    public void testBVT() throws Exception {
        // prepare data
        Long userId = 12345L;
        Long spouseId = 98765L;
        List<User> children = Arrays.asList(new User(111L), new User(222L));

        Set<User> parents = new HashSet<User>();
        parents.add(new User(99L));
        parents.add(new User(88L));

        List<Long> friends = Arrays.asList(123L, 234L);
        Set<Long> opponents = new HashSet<Long>();
        opponents.add(678L);
        opponents.add(789L);

        User user = new User();
        user.setUserId(userId);
        user.setSpouseId(spouseId);
        user.setChildren(children);
        user.setParents(parents);
        user.setFriends(friends);
        user.setOpponents(opponents);

        // do serialization
        String json = mapper.writeValueAsString(user);
        Assert.assertTrue(json.contains("href"));

        // do de-serialization
        User user2 = mapper.readValue(json, User.class);

        // validation
        Assert.assertEquals(userId, user2.getUserId(), "userId should match.");
        Assert.assertEquals(spouseId, user2.getSpouseId(), "spouseId should match.");
        Assert.assertEquals(children.size(), user2.getChildren().size(), "children size should match.");
        Assert.assertEquals(parents.size(), user2.getParents().size(), "parents size should match.");
        Assert.assertEquals(friends.size(), user2.getFriends().size(), "friends size should match.");
        Assert.assertEquals(opponents.size(), user2.getOpponents().size(), "opponents size should match.");
    }

    @Test
    public void testNull() throws Exception {
        User user = new User();
        user.setUserId(123L);

        String json = mapper.writeValueAsString(user);
        Assert.assertFalse(json.contains("No null occurred."));
    }

    @Test
    public void testStringId() throws Exception {
        User user = new User();
        user.setTest1("this_id_test_id");
        user.setTest2(Arrays.asList("123", "456"));

        String json = mapper.writeValueAsString(user);
        User user2 = mapper.readValue(json, User.class);

        Assert.assertEquals(user.getTest1(), user2.getTest1(), "test1 field should match.");
        Assert.assertEquals(user.getTest2().size(), user2.getTest2().size(), "test2 field size should match.");
    }

    @Test
    public void testListLong() throws Exception {
        User user = new User();
        user.setFriends(Arrays.asList(123L, 456L));

        String json = mapper.writeValueAsString(user);
        User user2 = mapper.readValue(json, User.class);

        Assert.assertEquals(123L, (long) (user2.getFriends().get(0)));
        Assert.assertEquals(456L, (long) (user2.getFriends().get(1)));
    }

    @Test
    public void testOrderId() throws Exception {
        User user = new User();
        user.setOrderId(1234567L);

        String json = mapper.writeValueAsString(user);
        User user2 = mapper.readValue(json, User.class);

        Assert.assertEquals(user.getOrderId(), user2.getOrderId(), "order id should match.");
    }

    @Test
    public void testCascadeId() throws Exception {
        PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.setUserId(12345L);
        paymentInstrument.setPaymentInstrumentId(99999L);

        String json = mapper.writeValueAsString(paymentInstrument);
        System.out.println(json);

        PaymentInstrument paymentInstrument2 = mapper.readValue(json, PaymentInstrument.class);

        Assert.assertEquals(paymentInstrument.getUserId(), paymentInstrument2.getUserId(), "user id should match.");

        Assert.assertEquals(paymentInstrument.getPaymentInstrumentId(),
                paymentInstrument2.getPaymentInstrumentId(),
                "user id should match.");
    }
}
