/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.jackson.deserializer.ResourceAwareDeserializationContext;
import com.junbo.common.jackson.serializer.ResourceAwareSerializerProvider;
import org.testng.Assert;
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
}
