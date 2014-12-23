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
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.UserId;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.common.util.Utils;
import com.junbo.configuration.ConfigServiceManager;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Hateoas serialization test.
 */
public class HateoasTest {
    private ObjectMapper mapper = ObjectMapperProvider.instance();
    private String urlPrefix = ConfigServiceManager.instance().getConfigValue("common.conf.resourceUrlPrefix");

    @BeforeTest
    public void setup() {
        assertTrue(urlPrefix != null && urlPrefix.equals("/v1"), "Url Prefix is invalid: " + urlPrefix);
    }

    @Test
    public void testStrongTypeId() throws Exception {
        // prepare data
        TestEntity testEntity = new TestEntity();
        testEntity.setUserId(new UserId(1L));
        testEntity.setOrderId(new OrderId(2L));

        // only partially set, the serialized object should not contain link
        String json = mapper.writeValueAsString(testEntity);
        JsonNode jsonNode = parseJson(json);
        assertAllLinksNull(testEntity);

        assertEquals(
                jsonNode.get("subLink1").get("href").asText(),
                Utils.combineUrl(urlPrefix, "/users/6b54ffb0bc9e/orders/3650-6702-5565"));

        assertEquals(jsonNode.get("subLink2").getNodeType(), JsonNodeType.NULL);
        assertEquals(jsonNode.get("superLink").getNodeType(), JsonNodeType.NULL);
        testDeserialize(testEntity, json);
    }

    @Test
    public void testAllIds() throws Exception {
        // parepare data
        TestEntity testEntity = new TestEntity();
        testEntity.setUserId(new UserId(1L));
        testEntity.setOrderId(new OrderId(2L));
        testEntity.setFriendUserId(204783934L);
        testEntity.setFriendOrderId(4102394596L);

        String json = mapper.writeValueAsString(testEntity);
        JsonNode jsonNode = parseJson(json);

        assertEquals(
                jsonNode.get("subLink1").get("href").asText(),
                Utils.combineUrl(urlPrefix, "/users/6b54ffb0bc9e/orders/3650-6702-5565"));

        assertEquals(
                jsonNode.get("subLink2").get("href").asText(),
                Utils.combineUrl(urlPrefix, "/friends/6355ef9dbda1/3687-3240-1275"));

        assertEquals(
                jsonNode.get("superLink").get("href").asText(),
                Utils.combineUrl(urlPrefix, "/users/6b54ffb0bc9e/orders/3650-6702-5565/friends/6355ef9dbda1/3687-3240-1275/end"));

        assertAllLinksNull(testEntity);
        testDeserialize(testEntity, json);
    }

    @Test
    public void testWeakTypeId() throws Exception {
        // prepare data
        TestEntity testEntity = new TestEntity();
        testEntity.setFriendUserId(1L);
        testEntity.setFriendOrderId(2L);
        testEntity.setUserId(null);
        testEntity.setOrderId(null);

        String json = mapper.writeValueAsString(testEntity);
        JsonNode jsonNode = parseJson(json);

        assertEquals(jsonNode.get("subLink1").getNodeType(), JsonNodeType.NULL);

        assertEquals(
                jsonNode.get("subLink2").get("href").asText(),
                Utils.combineUrl(urlPrefix, "/friends/6b54ffb0bc9e/3650-6702-5565"));

        assertEquals(jsonNode.get("superLink").getNodeType(), JsonNodeType.NULL);

        assertAllLinksNull(testEntity);
        testDeserialize(testEntity, json);

        testEntity.setFriendOrderId(null);
        json = mapper.writeValueAsString(testEntity);
        jsonNode = parseJson(json);

        // now none of the links are complete, all links are null
        assertEquals(jsonNode.get("subLink1").getNodeType(), JsonNodeType.NULL);
        assertEquals(jsonNode.get("subLink2").getNodeType(), JsonNodeType.NULL);
        assertEquals(jsonNode.get("superLink").getNodeType(), JsonNodeType.NULL);

        assertAllLinksNull(testEntity);
        testDeserialize(testEntity, json);
    }

    @Test
    public void testEnumId() throws Exception {
        TestEntity testEntity = new TestEntity();
        testEntity.setCurrencyId(new CurrencyId("USD"));
        testEntity.setFriendCurrencyId("JPY");

        String json = mapper.writeValueAsString(testEntity);
        JsonNode jsonNode = parseJson(json);

        assertEquals(jsonNode.get("subLink1").getNodeType(), JsonNodeType.NULL);
        assertEquals(jsonNode.get("subLink2").getNodeType(), JsonNodeType.NULL);
        assertEquals(jsonNode.get("superLink").getNodeType(), JsonNodeType.NULL);

        assertEquals(
                jsonNode.get("subLink3").get("href").asText(),
                Utils.combineUrl(urlPrefix, "/currencies/USD/JPY"));

        assertAllLinksNull(testEntity);
        testDeserialize(testEntity, json);

        testEntity.setFriendOrderId(null);
        json = mapper.writeValueAsString(testEntity);
        jsonNode = parseJson(json);

        // now none of the links are complete, all links are null
        assertEquals(jsonNode.get("subLink1").getNodeType(), JsonNodeType.NULL);
        assertEquals(jsonNode.get("subLink2").getNodeType(), JsonNodeType.NULL);
        assertEquals(jsonNode.get("superLink").getNodeType(), JsonNodeType.NULL);

        assertAllLinksNull(testEntity);
        testDeserialize(testEntity, json);
    }

    @Test(expectedExceptions = RuntimeException.class,
            expectedExceptionsMessageRegExp = "JsonProperty not allowed in @HateoasLink field: .*")
    public void testJsonPropertyNotAllowed() throws Exception {
        BadTestEntity testEntity = new BadTestEntity();
        mapper.writeValueAsString(testEntity);
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

    private void assertAllLinksNull(TestEntity testEntity) {
        assertNull(testEntity.getSubLink1());
        assertNull(testEntity.getSubLink2());
        assertNull(testEntity.getSubLink3());
        assertNull(testEntity.getSuperLink());
    }

    private void testDeserialize(TestEntity testEntity, String json) throws Exception {
        TestEntity testEntity2 = mapper.readValue(json, TestEntity.class);
        assertEquals(testEntity2.getUserId(), testEntity.getUserId());
        assertEquals(testEntity2.getOrderId(), testEntity.getOrderId());
        assertEquals(testEntity2.getFriendUserId(), testEntity.getFriendUserId());
        assertEquals(testEntity2.getFriendOrderId(), testEntity.getFriendOrderId());
        assertEquals(testEntity2.getCurrencyId(), testEntity.getCurrencyId());
        assertEquals(testEntity2.getFriendCurrencyId(), testEntity.getFriendCurrencyId());
        assertAllLinksNull(testEntity2);
    }
}
