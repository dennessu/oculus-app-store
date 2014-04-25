/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.hateoas;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.UserId;
import com.junbo.common.json.ObjectMapperProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Hateoas serialization test.
 */
public class HateoasTest {
    private ObjectMapper mapper = ObjectMapperProvider.instance();

    @Test
    public void testBVT() throws Exception {
        // prepare data
        TestEntity testEntity = new TestEntity();
        testEntity.setUserId(new UserId(1L));
        testEntity.setOrderId(new OrderId(2L));

        // only partially set, the serialized object should not contain link
        String json = mapper.writeValueAsString(testEntity);
        JsonNode jsonNode = parseJson(json);

        assertEquals(
                jsonNode.get("subLink1").get("href").asText(),
                "https://api.oculusvr.com/v1/users/6B54FFB0BC9E/orders/3650-6702-5565");

        assertEquals(jsonNode.get("subLink2").getNodeType(), JsonNodeType.NULL);
        assertEquals(jsonNode.get("superSuperLink").getNodeType(), JsonNodeType.NULL);

        TestEntity testEntity2 = mapper.readValue(json, TestEntity.class);
        assertEquals(testEntity2.getUserId().getValue(), testEntity.getUserId().getValue());
        assertEquals(testEntity2.getOrderId().getValue(), testEntity.getOrderId().getValue());

        testEntity.setFriendUserId(204783934L);
        testEntity.setFriendOrderId(4102394596L);
        json = mapper.writeValueAsString(testEntity);
        jsonNode = parseJson(json);

        assertEquals(
                jsonNode.get("subLink1").get("href").asText(),
                "https://api.oculusvr.com/v1/users/6B54FFB0BC9E/orders/3650-6702-5565");

        assertEquals(
                jsonNode.get("subLink2").get("href").asText(),
                "https://api.oculusvr.com/v1/friends/6355EF9DBDA1/3687-3240-1275");

        assertEquals(
                jsonNode.get("superSuperLink").get("href").asText(),
                "https://api.oculusvr.com/v1/users/6B54FFB0BC9E/orders/3650-6702-5565/friends/6355EF9DBDA1/3687-3240-1275/end");

        testEntity.setFriendUserId(1L);
        testEntity.setFriendOrderId(2L);
        testEntity.setUserId(null);
        testEntity.setOrderId(null);
        json = mapper.writeValueAsString(testEntity);
        jsonNode = parseJson(json);

        assertEquals(jsonNode.get("subLink1").getNodeType(), JsonNodeType.NULL);

        assertEquals(
                jsonNode.get("subLink2").get("href").asText(),
                "https://api.oculusvr.com/v1/friends/6B54FFB0BC9E/3650-6702-5565");

        assertEquals(jsonNode.get("superSuperLink").getNodeType(), JsonNodeType.NULL);

        testEntity.setFriendOrderId(null);
        json = mapper.writeValueAsString(testEntity);
        jsonNode = parseJson(json);

        // now none of the links are complete, all links are null
        assertEquals(jsonNode.get("subLink1").getNodeType(), JsonNodeType.NULL);
        assertEquals(jsonNode.get("subLink2").getNodeType(), JsonNodeType.NULL);
        assertEquals(jsonNode.get("superSuperLink").getNodeType(), JsonNodeType.NULL);
    }

    private JsonNode parseJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonFactory factory = mapper.getFactory();
            JsonParser jp = factory.createParser(json);
            return mapper.readTree(jp);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to parse json: " + json, ex);
        }
    }
}
